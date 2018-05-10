package com.perfecto.reporting.sample;

import com.perfecto.reportium.model.CustomField;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/*
 * Source control integration
 * --------------------------
 * If your build environment supports integration with version control systems, you should be able to automatically
 * set the repository URL and commit values to custom fields in the CI job's build command.  For example, Jenkin's GIT
 * plugin sets these values to the GIT_URL and GIT_COMMIT environment variables, so you can add the following VM option:
 *    -DReportiumCustomFields=perfecto.vcs.repositoryUrl=${GIT_URL},perfecto.vcs.commit=${GIT_COMMIT}
 *
 * For more information see https://developers.perfectomobile.com/display/PD/Accessing+Source+Code
 */
public class VcsUtils {

    private static final String VCS_URL_KEY = "vcs-url";
    private static final String VCS_COMMIT_KEY = "vcs-commit";

    private static final String VCS_FIELD_PREFIX = "perfecto.vcs.";
    private static final String VCS_URL_FIELD = VCS_FIELD_PREFIX + "repositoryUrl";
    private static final String VCS_COMMIT_FIELD = VCS_FIELD_PREFIX + "commit";
    private static final String VCS_FILE_PATH_FIELD = VCS_FIELD_PREFIX + "filePath";

    private static final String SOURCE_FILE_EXTENSION = ".java";

    public static CustomField[] addVcsFields(String sourceFileRootPath, CustomField... userCustomFields) {
        CustomField[] vcsFields = createVcsFields(sourceFileRootPath);
        CustomField[] customFields = mergeCustomFields(vcsFields, userCustomFields);
        return customFields;
    }

    public static CustomField[] createVcsFields(String sourceFileRootPath) {
        String testClassName = getCallingClassName();
        CustomField[] vcsFields = createVcsFields(sourceFileRootPath, testClassName);
        return vcsFields;
    }

    public static CustomField[] createVcsFields(String sourceFileRootPath, String testClassName) {
        // Repository URL
        String repositoryUrl = System.getProperty(VCS_URL_KEY);
        if (repositoryUrl == null || repositoryUrl.isEmpty()) {
            // No source control integration without repository URL
            return new CustomField[0];
        }
        List<CustomField> customFields = new LinkedList<>();
        customFields.add(new CustomField(VCS_URL_FIELD, repositoryUrl));

        // Commit
        String commit = System.getProperty(VCS_COMMIT_KEY);
        if (commit != null && !commit.isEmpty()) {
            customFields.add(new CustomField(VCS_COMMIT_FIELD, commit));
        }

        // File path
        String relativeFilePath = testClassName.replace('.', '/');
        String fullFilePath = sourceFileRootPath + "/" + relativeFilePath + SOURCE_FILE_EXTENSION;
        customFields.add(new CustomField(VCS_FILE_PATH_FIELD, fullFilePath));

        CustomField[] vcsFields = customFields.toArray(new CustomField[0]);
        return vcsFields;
    }

    private static CustomField[] mergeCustomFields(CustomField[] vcsFields, CustomField... userCustomFields) {
        if (vcsFields == null || vcsFields.length == 0) {
            return userCustomFields;
        }
        if (userCustomFields.length == 0) {
            return vcsFields;
        }
        CustomField[] allCustomFields = Arrays.copyOf(vcsFields, vcsFields.length + userCustomFields.length);
        System.arraycopy(userCustomFields, 0, allCustomFields, vcsFields.length, userCustomFields.length);
        return allCustomFields;
    }

    private static String getCallingClassName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int index = 1;
        String thisClassName = VcsUtils.class.getName();
        StackTraceElement stackTraceElement;
        do {
            index++;
            stackTraceElement = stackTrace[index];
        } while (stackTraceElement.getClassName().equals(thisClassName));
        String callingClassName = stackTraceElement.getClassName();
        return callingClassName;
    }

}
