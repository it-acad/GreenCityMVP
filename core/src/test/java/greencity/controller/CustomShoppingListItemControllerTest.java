package greencity.controller;

import greencity.dto.shoppinglistitem.BulkSaveCustomShoppingListItemDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.enums.ShoppingListItemStatus;
import greencity.service.CustomShoppingListItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CustomShoppingListItemControllerTest {
    private static final String customShoppingListItemControllerLink = "/custom/shopping-list-items";
    private MockMvc mockMvc;

    @InjectMocks
    CustomShoppingListItemController customShoppingListItemController;

    @Mock
    CustomShoppingListItemService customShoppingListItemService;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(customShoppingListItemController)
                .build();
    }

    @Test
    void getAllAvailableCustomShoppingListItems() throws Exception {
        Long userID = 1L;
        Long habitId = 2L;
        List<CustomShoppingListItemResponseDto> expectedResponse = Arrays.asList(new CustomShoppingListItemResponseDto());
        when(customShoppingListItemService.findAllAvailableCustomShoppingListItems(userID, habitId)).thenReturn(expectedResponse);

        mockMvc.perform(get(customShoppingListItemControllerLink + "/{userId}/{habitId}", userID, habitId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void saveUserCustomShoppingListItems() throws Exception {
        Long userId = 1L;
        Long habitAssignId = 2L;
        BulkSaveCustomShoppingListItemDto dto = new BulkSaveCustomShoppingListItemDto();
        List<CustomShoppingListItemResponseDto> expectedResponse = Arrays.asList(new CustomShoppingListItemResponseDto());
        when(customShoppingListItemService.save(dto, userId, habitAssignId)).thenReturn(expectedResponse);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(dto);

        mockMvc.perform(post(customShoppingListItemControllerLink + "/{userId}/{habitAssignId}/custom-shopping-list-items" , 1L , 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void updateItemStatus() throws Exception{
        Long userId = 1L;
        Long itemId = 2L;
        String itemStatus = "DONE";
        CustomShoppingListItemResponseDto expectedResponse = new CustomShoppingListItemResponseDto();
        expectedResponse.setStatus(ShoppingListItemStatus.valueOf(itemStatus));

        when(customShoppingListItemService.updateItemStatus(userId, itemId, itemStatus)).thenReturn(expectedResponse);

        mockMvc.perform(patch(customShoppingListItemControllerLink + "/{userId}/custom-shopping-list-items" , 1L)
                        .param("itemId", String.valueOf(itemId))
                        .param("status", itemStatus)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updateItemStatusToDone() throws Exception{
        Long userId = 1L;
        Long itemId = 2L;

        mockMvc.perform(patch(customShoppingListItemControllerLink + "/{userId}/done", userId)
                        .param("itemId", String.valueOf(itemId)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void bulkDeleteCustomShoppingListItems() throws Exception{
        String ids = "1,2";
        Long userId = 1L;
        List<Long> expectedResponse = Arrays.asList(1L, 2L);
        when(customShoppingListItemService.bulkDelete(ids)).thenReturn(expectedResponse);

        mockMvc.perform(delete(customShoppingListItemControllerLink + "/{userId}/custom-shopping-list-items", userId)
                        .param("ids", ids)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllCustomShoppingItemsByStatus() throws Exception{
        Long userId = 1L;
        String status = "Some status";

        List<CustomShoppingListItemResponseDto> expectedResponse = Arrays.asList(new CustomShoppingListItemResponseDto());

        when(customShoppingListItemService.findAllUsersCustomShoppingListItemsByStatus(userId, status)).thenReturn(expectedResponse);

        mockMvc.perform(get(customShoppingListItemControllerLink + "/{userId}/custom-shopping-list-items", userId)
                        .param("status", status)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllCustomShoppingItemsByStatus_NoStatus() throws Exception {
        Long userId = 1L;
        List<CustomShoppingListItemResponseDto> expectedResponse = Arrays.asList(new CustomShoppingListItemResponseDto());

        when(customShoppingListItemService.findAllUsersCustomShoppingListItemsByStatus(userId, null)).thenReturn(expectedResponse);

        mockMvc.perform(get(customShoppingListItemControllerLink + "/{userId}/custom-shopping-list-items", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

