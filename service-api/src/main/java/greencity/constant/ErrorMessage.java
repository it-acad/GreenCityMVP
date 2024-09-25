package greencity.constant;

public final class ErrorMessage {
    public static final String CUSTOM_SHOPPING_LIST_ITEM_NOT_FOUND_BY_ID =
        "Custom shopping list item with such id does not exist.";
    public static final String DUPLICATED_CUSTOM_SHOPPING_LIST_ITEM = "CustomShoppingListItems should be unique";
    public static final String SHOPPING_LIST_ITEM_NOT_DELETED = "Advice not deleted ";
    public static final String SHOPPING_LIST_ITEM_NOT_FOUND_BY_ID = "Shopping list item with such id does not exist ";
    public static final String SHOPPING_LIST_ITEM_NOT_ASSIGNED_FOR_THIS_HABIT =
        "Shopping list item with such id does not assigned fot this habit ";
    public static final String SHOPPING_LIST_ITEM_ALREADY_SELECTED =
        "Shopping list item with such id is already selected ";
    public static final String SHOPPING_LIST_ITEM_NOT_FOUND_BY_NAMES =
        "Shopping list item with such name(s) does not exist: ";
    public static final String PARSING_URL_FAILED = "Can't parse image's url: ";
    public static final String HABIT_STATISTIC_ALREADY_EXISTS = "Habit statistic already exists with such date";
    public static final String HABIT_ASSIGN_NOT_FOUND_BY_ID = "Habit assign does not exist by this id : ";
    public static final String USER_ALREADY_HAS_MAX_NUMBER_OF_HABIT_ASSIGNS =
        "User has reached the limit of active habit assigns: ";
    public static final String HABIT_STATISTIC_NOT_FOUND_BY_ID = "Habit statistic does not exist by this id : ";
    public static final String HABIT_NOT_FOUND_BY_ID = "Habit does not exist by this id : ";
    public static final String WRONG_DATE = "Can't create habit statistic for such date";
    public static final String HABIT_TRANSLATION_NOT_FOUND = "Habit translation not found for habit with id : ";
    public static final String SHOPPING_LIST_ITEM_TRANSLATION_NOT_FOUND =
        "Shopping list item translation not found for habit with id : ";
    public static final String INVALID_LANGUAGE_CODE = "Given language code is not supported.";

    public static final String CATEGORY_NOT_FOUND_BY_ID = "The category does not exist by this id: ";
    public static final String CATEGORY_NOT_FOUND_BY_NAME = "The category does not exist by this name: ";
    public static final String CATEGORY_ALREADY_EXISTS_BY_THIS_NAME = "Category by this name already exists.";
    public static final String CANNOT_ADD_PARENT_CATEGORY = "The category cannot be added to no-parent category";

    public static final String TAG_NOT_DELETED = "Tag not deleted by id : ";
    public static final String TAG_NOT_FOUND = "Tag not found by id : ";
    public static final String TAGS_NOT_FOUND = "There should be at least one valid tag";
    public static final String DUPLICATED_TAG = "Tags should be unique";
    public static final String INVALID_NUM_OF_TAGS =
        "Invalid tags. You must have less than " + ServiceValidationConstants.MAX_AMOUNT_OF_TAGS + " tags";
    public static final String FACT_OF_THE_DAY_NOT_FOUND = "The fact of the day not found: ";
    public static final String FACT_OF_THE_DAY_NOT_DELETED = "The fact of the day does not deleted by id: ";
    public static final String FACT_OF_THE_DAY_NOT_UPDATED = "The fact of the day does not updated by id: ";
    public static final String FACT_OF_THE_DAY_PROPERTY_NOT_FOUND =
        "For type Fact of the day  not found this property :";
    public static final String NEWS_SUBSCRIBER_EXIST = "Subscriber with this email address exists in the database.";
    public static final String NEWS_SUBSCRIBER_BY_EMAIL_NOT_FOUND =
        "Subscriber with this email address not found in the database.";
    public static final String HABIT_HAS_BEEN_ALREADY_ENROLLED = "You can enroll habit only once a day";
    public static final String HABIT_ALREADY_ACQUIRED = "You have already acquired habit with id: ";
    public static final String HABIT_IS_NOT_ENROLLED_ON_CURRENT_DATE = "Habit is not enrolled on ";
    public static final String HABIT_ASSIGN_NOT_FOUND_WITH_CURRENT_USER_ID_AND_HABIT_ID =
        "There is no habit assign for current user and such habit with id: ";
    public static final String HABIT_ASSIGN_NOT_FOUND_WITH_CURRENT_USER_ID_AND_HABIT_ASSIGN_ID =
        "There is no habit assign for current user and such habit assign id: ";
    public static final String HABIT_ASSIGN_NOT_FOUND_WITH_CURRENT_USER_ID_AND_HABIT_ID_AND_INPROGRESS_STATUS =
        "There is no inprogress habit assign for current user and such habit with id: ";
    public static final String HABIT_ASSIGN_NOT_FOUND_WITH_CURRENT_USER_ID_AND_INPROGRESS_STATUS =
        "There is no inprogress habit assign for current user: ";
    public static final String HABIT_STATUS_CALENDAR_OUT_OF_ENROLL_RANGE =
        "Can't enroll habit because date input is not in a range from today to it's 7 passed days";
    public static final String HABIT_STATISTIC_NOT_BELONGS_TO_USER =
        "Current user does not have habit statistic with id: ";
    public static final String USER_ALREADY_HAS_ASSIGNED_HABIT = "Current user already has assigned habit with id: ";
    public static final String USER_SUSPENDED_ASSIGNED_HABIT_FOR_CURRENT_DAY_ALREADY =
        "User already assigned and suspended this habit for today with id: ";
    public static final String HABIT_FACT_NOT_FOUND_BY_ID = "The habitfact does not exist by id: ";
    public static final String HABIT_FACT_NOT_DELETED_BY_ID = "The habitfact does not deleted by id: ";
    public static final String SPECIFICATION_VALUE_NOT_FOUND_BY_ID =
        "The specification value does not exist by this id: ";
    public static final String SPECIFICATION_NOT_FOUND_BY_NAME = "The specification does not exist by this name: ";
    public static final String FILE_NOT_SAVED = "File hasn't been saved";
    public static final String USER_NOT_FOUND_BY_ID = "The user does not exist by this id: ";
    public static final String USER_NOT_FOUND_BY_EMAIL = "The user does not exist by this email: ";
    public static final String USER_HAS_NO_SHOPPING_LIST_ITEMS =
        "This user hasn't selected any shopping list items yet";
    public static final String USER_SHOPPING_LIST_ITEM_NOT_FOUND = "UserShoppingListItem(s) with this id not found: ";
    public static final String USER_SHOPPING_LIST_ITEM_NOT_FOUND_BY_USER_ID =
        "UserShoppingListItem(s) for this user not found";
    public static final String DUPLICATED_USER_SHOPPING_LIST_ITEM = "UserShoppingListItems should be unique";
    public static final String USER_CANT_UPDATE_HIMSELF = "User can't update yourself";
    public static final String IMPOSSIBLE_UPDATE_USER_STATUS = "Impossible to update status of admin or moderator";
    public static final String PROFILE_PICTURE_NOT_FOUND_BY_ID = "Profile picture not found by id : ";
    public static final String IMAGE_EXISTS = "Image should be download, PNG or JPEG ";
    public static final String OWN_USER_ID = "You can not perform actions with your own id : ";
    public static final String NOT_FOUND_REQUEST = "Not found friend request from user with id: ";
    public static final String NOT_FOUND_ANY_FRIENDS = "Not found any friends by id: ";
    public static final String CUSTOM_SHOPPING_LIST_ITEM_WHERE_NOT_SAVED =
        "This CustomShoppingListItem(s) already exist(s): ";
    public static final String CUSTOM_SHOPPING_LIST_ITEM_WITH_THIS_ID_NOT_FOUND =
        "CustomShoppingListItem(s) with this id not found: ";
    public static final String CUSTOM_SHOPPING_LIST_ITEM_NOT_FOUND =
        "The user doesn't have any custom shopping list item.";
    public static final String USER_HAS_NO_PERMISSION = "Current user has no permission for this action";
    public static final String ECO_NEWS_NOT_FOUND_BY_ID = "Eco news doesn't exist by this id: ";
    public static final String ECO_NEWS_NOT_FOUND = "Eco news haven't been found";
    public static final String ECO_NEWS_NOT_SAVED = "Eco news haven't been saved because of constraint violation";
    public static final String EVENT_NOT_SAVED = "Event haven't been saved because of constraint violation";
    public static final String USER_ALREADY_JOINED_EVENT = "User has already joined the event ";
    public static final String AUTHOR_CANNOT_LEAVE_EVENT = "Author cannot leave the event ";
    public static final String USER_CANNOT_ADD_MORE_THAN_5_SOCIAL_NETWORK_LINKS =
        "User cannot add more than 5 social network links";
    public static final String INVALID_URI = "The string could not be parsed as a URI reference.";
    public static final String MALFORMED_URL = "Malformed URL. The string could not be parsed.";
    public static final String USER_CANNOT_ADD_SAME_SOCIAL_NETWORK_LINKS =
        "User cannot add the same social network links";
    public static final String COMMENT_NOT_FOUND_EXCEPTION = "The comment with entered id doesn't exist";
    public static final String CANNOT_REPLY_THE_REPLY = "Can not make a reply to a reply";
    public static final String NOT_A_CURRENT_USER = "You can't perform actions with the data of other user";
    public static final String USER_SHOPPING_LIST_ITEMS_STATUS_IS_ALREADY_DONE =
        "The status of this shopping list item is already done ";
    public static final String USER_DEACTIVATED = "User is deactivated";
    public static final String NO_ANY_EMAIL_TO_VERIFY_BY_THIS_TOKEN = "No any email to verify by this token";
    public static final String EMAIL_TOKEN_EXPIRED = "User late with verify. Token is invalid.";
    public static final String PASSWORD_RESTORE_LINK_ALREADY_SENT =
        "Password restore link already sent, please check your email: ";
    public static final String EMAIL_CANT_BE_NULL = "Email can't be null";
    public static final String REFRESH_TOKEN_NOT_VALID = "Refresh token not valid!";
    public static final String BAD_PASSWORD = "Bad password";
    public static final String USER_ALREADY_REGISTERED_WITH_THIS_EMAIL = "User with this email is already registered";
    public static final String PASSWORDS_DO_NOT_MATCHES = "The passwords don't matches";
    public static final String PASSWORD_DOES_NOT_MATCH = "The password doesn't match";
    public static final String USER_HAS_BLOCKED_STATUS = "User has blocked status.";
    public static final String WRONG_DATE_TIME_FORMAT =
        "The date format is wrong. Should matches " + AppConstant.DATE_FORMAT;
    public static final String INVALID_DATE_RANGE = "The 'From' date must be earlier than the 'To' date";
    public static final String SELECT_CORRECT_LANGUAGE = "Select correct language: 'en', 'ua' or 'ru'";
    public static final String INVALID_HABIT_ID = "Invalid habit id ";
    public static final String WRONG_COUNT_OF_TAGS_EXCEPTION =
        "Count of tags should be at least one but not more three";
    public static final String TOKEN_FOR_RESTORE_IS_INVALID = "Token is null or it doesn't exist.";
    public static final String PAGE_INDEX_IS_MORE_THAN_TOTAL_PAGES = "Page index is more than total pages: ";
    public static final String MULTIPART_FILE_BAD_REQUEST =
        "Can`t convert To Multipart Image. Bad inputed image string : ";
    public static final String INCORRECT_INPUT_ITEM_STATUS = "Incorrect input status to update item.";
    public static final String HABIT_ASSIGN_STATUS_IS_NOT_INPROGRESS_OR_USER_HAS_NOT_ANY_ASSIGNED_HABITS =
        "Habit assign status is not INPROGRESS or user has not any assigned habits";
    public static final String INVALID_SORTING_VALUE = "Supported sort is: asc|desc";
    public static final String FILTER_NOT_FOUND_BY_ID = "Filter not found";
    public static final String USER_HAS_NO_FRIEND_WITH_ID = "User has no friend with this id: ";
    public static final String INVALID_DURATION = "The duration for such habit is lower than previously set";
    public static final String NOTIFICATION_ALREADY_READ = "This notification is already read";
    public static final String USER_ID_CANNOT_BE_NULL = "User ID cannot be null";
    public static final String NOTIFICATION_NOT_FOUND_BY_ID = "Notification with ID not found: ";
    public static final String NOTIFICATION_DTO_CANNOT_BE_NULL = "Notification DTO cannot be null";
    public static final String NOTIFICATION_SECTION_CANNOT_BE_NULL_OR_EMPTY = "Notification section cannot be null or empty";
    public static final String NOTIFICATION_SECTION_TYPE_CANNOT_BE_NULL_OR_EMPTY = "Notification section type cannot be null or empty";
    public static final String NOTIFICATION_TEXT_CANNOT_BE_NULL_OR_EMPTY = "Notification text cannot be null or empty";
    public static final String CANNOT_MAP_NULL_TO_DTO = "Cannot map null to DTO";
    public static final String CANNOT_MAP_NULL_TO_ENTITY = "Cannot map null to entity";
    public static final String COMMENT_NOT_FOUND_BY_ID = "Comment with id not found: ";
    public static final String ENABLE_TO_CONTAIN_URL = "Content can't contain URL";
    public static final String ENABLE_TO_CONTAIN_EMOJI = "Content can't contain emoji";
    public static final String INVALID_COMMENT_ID = "Invalid comment id: ";
    public static final String ENABLE_TO_CONTAIN_ONLY_ALLOWED_CHARACTERS = "Content can contain only allowed characters";
    public static final String EVENT_NOT_FOUND = "Event not found with id: ";
    public static final String EVENT_DAY_DETAILS_NOT_FOUND = "Event day details not found with id: ";
    public static final String EVENT_EDIT_DTO_ERROR = "Wrong body request format for DTO or null";

    private ErrorMessage() {
    }
}
