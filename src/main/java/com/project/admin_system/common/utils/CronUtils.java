package com.project.admin_system.common.utils;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import java.util.Locale;

public class CronUtils {
    private static final CronParser CRON_PARSER = new CronParser(
            CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING)
    );

    private CronUtils() {
    }

    private static final CronDescriptor CRON_DESCRIPTOR = CronDescriptor.instance(Locale.KOREAN);

    public static String toReadable(String cron) {
        return CRON_DESCRIPTOR.describe(CRON_PARSER.parse(cron));
    }

    public static Cron parse(String cron) {
        return CRON_PARSER.parse(cron);
    }
}
