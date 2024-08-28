package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.event.EventCreationDtoRequest;
import greencity.dto.event.EventDto;
import greencity.entity.Event;
import greencity.entity.EventImage;
import greencity.entity.User;
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
import java.util.List;

import static greencity.constant.AppConstant.DEFAULT_EVENT_IMAGE;


@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepo eventRepo;
    private final UserRepo userRepo;
    private final FileService fileService;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public EventDto saveEvent(EventCreationDtoRequest eventCreationDtoRequest,
                              List<MultipartFile> images,
                              String userEmail) {

        // Find the user by email
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + userEmail));

        // Map eventCreationDtoRequest to Event entity using EventCreationDtoMapper
        Event eventToSave = modelMapper.map(eventCreationDtoRequest, Event.class);
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

        // If the list of images is empty, add the default image
        if (images == null || images.isEmpty()) {
            eventImages.add(new EventImage(DEFAULT_EVENT_IMAGE));
        } else {
            // Iterate through the list of images and upload each one
            for (MultipartFile image : images) {
                String imagePath = fileService.upload(image);
                eventImages.add(new EventImage(imagePath));
            }
        }

        // Return the list of uploaded event images
        return eventImages;
    }

}