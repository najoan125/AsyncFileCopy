package kr.hyfata.najoan.async.filecopy.handler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonParser {
    private final JSONArray copies;

    public JsonParser(String jsonPath) throws IOException {
        String jsonContent = new String(Files.readAllBytes(Paths.get(jsonPath)));
        JSONObject jsonObject = new JSONObject(jsonContent);
        copies =  jsonObject.getJSONArray("copies");
    }

    public int getCopiesLength() {
        return copies.length();
    }

    public String getSourcePath(int index) {
        JSONObject copy = getCopy(index);
        return expandEnvironmentVariables(copy.getString("source"));
    }

    public String getDestinationPath(int index) {
        JSONObject copy = getCopy(index);
        return expandEnvironmentVariables(copy.getString("destination"));
    }

    private JSONObject getCopy(int index) {
        return copies.getJSONObject(index);
    }

    private String expandEnvironmentVariables(String path) {
        if (path == null) {
            return null;
        }

        StringBuilder result = new StringBuilder();
        int startIndex = 0;
        int endIndex;

        while ((startIndex = path.indexOf('%', startIndex)) >= 0) {
            if ((endIndex = path.indexOf('%', startIndex + 1)) >= 0) {
                String envVar = path.substring(startIndex + 1, endIndex);
                String envValue = System.getenv(envVar);

                if (envValue != null) {
                    result.append(path, 0, startIndex).append(envValue);
                } else {
                    result.append(path, 0, endIndex + 1);
                }
                path = path.substring(endIndex + 1);
                startIndex = 0;
            } else {
                break;
            }
        }

        result.append(path);
        return result.toString();
    }
}
