package greencity.controller;

import greencity.converters.UserArgumentResolver;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.dto.user.UserShoppingListItemResponseDto;
import greencity.dto.user.UserVO;
import greencity.service.ShoppingListItemService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.validation.Validator;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static greencity.ModelUtils.getPrincipal;
import static greencity.ModelUtils.getUserVO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ShoppingListItemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ShoppingListItemService shoppingListItemService;

    @Mock
    private Validator mockValidator;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ShoppingListItemController shoppingListItemController;
    private static final String initialUrl = "/user/shopping-list-items";
    private final Principal principal = getPrincipal();
    private final UserVO userVO = getUserVO();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(shoppingListItemController)
                .setValidator(mockValidator)
                .setCustomArgumentResolvers(new UserArgumentResolver(userService, modelMapper))
                .build();
    }

    @Test
    void bulkDeleteUserShoppingListItem_StatusOk() throws Exception {

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(delete(initialUrl)
                        .param("habitId", "1")
                        .param("shoppingListItemIds", "1,2")
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void saveUserShoppingListItemStatusWithoutLanguageParamTest_statusIsCreated() throws Exception {
        List<UserShoppingListItemResponseDto> listItemResponseDtos = new ArrayList<>();

        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(shoppingListItemService.saveUserShoppingListItems(anyLong(), anyLong(), anyList(), anyString()))
                .thenReturn(listItemResponseDtos);

        mockMvc.perform(post(initialUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]")
                        .param("habitId", "1")
                        .principal(principal))
                .andExpect(status().isCreated());
    }

    @Test
    void updateUserShoppingListItemStatusWithoutLanguageParamTest_statusIsCreated() throws Exception {
        Long userShoppingListItemId = 1L;
        String status = "DONE";

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(patch(initialUrl + "/{userShoppingListItemId}/status/{status}",
                        userShoppingListItemId, status)
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void updateUserShoppingListItemStatusWithLanguageParamTest_statusIsCreated() throws Exception {
        Long userShoppingListItemId = 1L;
        String status = "DONE";
        Locale locale = Locale.ENGLISH;

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(patch(initialUrl + "/{userShoppingListItemId}/status/{status}",
                        userShoppingListItemId, status)
                        .param("lang", locale.getLanguage())
                        .principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    void getUserShoppingListItemsWithLanguageParamTest_statusIsOk() throws Exception {
        Long habitId = 1L;
        Locale locale = Locale.ENGLISH;

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(initialUrl + "/habits/{habitId}/shopping-list", habitId)
                        .param("lang", locale.getLanguage())
                        .principal(principal))
                .andExpect(status().isOk());

        verify(shoppingListItemService).getUserShoppingList(userVO.getId(), habitId, locale.getLanguage());
    }

    @Test
    void getUserShoppingListItemWithoutLanguageParamTest_statusIsOk() throws Exception {
        Long habitId = 1L;

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(initialUrl + "/habits/{habitId}/shopping-list", habitId)
                        .principal(principal))
                .andExpect(status().isOk());

    }

    @Test
    void deleteShoppingListItem_statusOk() throws Exception {

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(delete(initialUrl)
                        .param("habitId", "1")
                        .param("shoppingListItemId", "1")
                        .principal(principal))
                .andExpect(status().isOk());

        verify(shoppingListItemService).deleteUserShoppingListItemByItemIdAndUserIdAndHabitId(1L, userVO.getId(), 1L);
    }

    @Test
    void findInProgressByUserId_StatusOk() throws Exception {
        Long userId = 1L;
        String languageCode = "en";
        List<ShoppingListItemDto> responseDtoList = List.of(new ShoppingListItemDto());

        when(shoppingListItemService.findInProgressByUserIdAndLanguageCode(userId, languageCode))
                .thenReturn(responseDtoList);

        mockMvc.perform(get(initialUrl + "/{userId}/get-all-inprogress", userId)
                        .param("lang", languageCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(shoppingListItemService, times(1)).findInProgressByUserIdAndLanguageCode(userId, languageCode);
    }
}
