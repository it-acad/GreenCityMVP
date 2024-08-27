package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.event.EventCreationDtoRequest;
import greencity.dto.event.EventDayDetailsDto;
import greencity.dto.event.EventDto;
import greencity.dto.tag.TagVO;
import greencity.entity.Event;
import greencity.entity.EventDayDetails;
import greencity.entity.EventImage;
import greencity.entity.User;
import greencity.enums.EventType;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.repository.EventRepo;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static greencity.constant.AppConstant.DEFAULT_EVENT_IMAGE;


@Service
@RequiredArgsConstructor
public class EventServiceImp implements EventService {
    private final EventRepo eventRepo;
    private final UserRepo userRepo;
    private final FileService fileService;
    private final TagsService tagService;
    private final ModelMapper modelMapper;

    private static final int MAX_IMAGES = 5;


    @Override
    @Transactional
    public EventDto saveEvent(EventCreationDtoRequest eventCreationDtoRequest, List<MultipartFile> images, String userEmail) {

        // Find the user by email
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + userEmail));

        // Convert eventCreationDtoRequest to Event entity
        Event eventToSave = modelMapper.map(eventCreationDtoRequest, Event.class);
        eventToSave.setEventType(EventType.valueOf(eventCreationDtoRequest.getEventType().toUpperCase()));
        eventToSave.setAuthor(user);

        // Handle images
        List<EventImage> eventImages = uploadImages(images);
        eventToSave.setImages(eventImages);

        // Attempt to save the event
        try {
            Event savedEvent = eventRepo.save(eventToSave);
            return modelMapper.map(savedEvent, EventDto.class);
        } catch (DataIntegrityViolationException e) {
            throw new NotSavedException(ErrorMessage.EVENT_NOT_SAVED);
        }
    }

    private List<EventImage> uploadImages(List<MultipartFile> images) {
        // Initialize the list to store the event images
        List<EventImage> eventImages = new ArrayList<>();

        // Check if the number of images exceeds the maximum allowed limit
        if (images.size() > MAX_IMAGES) {
            throw new IllegalArgumentException("You can upload up to " + MAX_IMAGES + " images.");
        }

        // Iterate through the list of images and upload each one
        for (MultipartFile image : images) {
            String imagePath = fileService.upload(image);
            eventImages.add(new EventImage(imagePath));
        }

        // If no images were uploaded, add the default image
        if (eventImages.isEmpty()) {
            eventImages.add(new EventImage(DEFAULT_EVENT_IMAGE));
        }

        // Return the list of uploaded event images
        return eventImages;
    }

}