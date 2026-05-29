package platform.driver.service.interfaces.models.constant;

/**
 * Interfaces-layer validation constants used by HTTP DTOs.
 */
public class ApplicationConstant {

    public static final String REQUEST_ID_REGREX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    public static final String DATETIME_REGREX = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}[-+]\\d{2}:\\d{2}";
    public static final String CHANNEL_REGREX = "^(ONL|OFF)$";
    public static final String ONLY_NUMBER_REGEX = "^\\d+$";
    public static final String ONLY_CHARACTER_REGEX = "^[\\p{L} ]+$";
    public static final String ONLY_NUMBER_AND_CHARACTER_DIGITS = "^[A-Za-z0-9]+$";
    public static final String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    public static final String DATE_MONTH_YEAR_REGEX = "^(19|20)\\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$";

}

