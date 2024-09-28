package org.translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An implementation of the Translator interface which reads in the translation
 * data from a JSON file. The data is read in once each time an instance of this class is constructed.
 */
public class JSONTranslator implements Translator {

    private JSONArray jsonData;
    private Map<String, String> countryTranslations;
    private Map<String, List<String>> countryLanguages;

    /**
     * Constructs a JSONTranslator using data from the sample.json resources file.
     */
    public JSONTranslator() {
        this("sample.json");
    }

    /**
     * Constructs a JSONTranslator populated using data from the specified resources file.
     * @param filename the name of the file in resources to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public JSONTranslator(String filename) {
        // read the file to get the data to populate things...
        try {
            String jsonString = Files.readString(Paths.get(getClass().getClassLoader().getResource(filename).toURI()));

            JSONArray jsonArray = new JSONArray(jsonString);

            countryTranslations = new HashMap<>();
            countryLanguages = new HashMap<>();

            for (int i = 0; i < jsonData.length(); i++) {
                JSONObject countryObj = jsonData.getJSONObject(i);

                String countryCode = countryObj.getString("alpha3");
                String countryName = countryObj.getString("name");

                countryTranslations.put(countryCode, countryName);

                List<String> languages = new ArrayList<>();
                for (String key : countryObj.keySet()) {
                    if (key.length() == 2) {
                        languages.add(key);
                    }
                }
                countryLanguages.put(countryCode, languages);
            }

        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getCountryLanguages(String country) {
        List<String> languages = countryLanguages.get(country);

        if (languages == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(languages);
    }

    @Override
    public List<String> getCountries() {
        List<String> countries = new ArrayList<>(countryTranslations.keySet());

        return new ArrayList<>(countries);
    }

    @Override
    public String translate(String country, String language) {
        String translation = "Translation not available for this language";

        if (!countryLanguages.containsKey(country)) {
            translation = "Unknown country";
        }
        else {
            List<String> languages = countryLanguages.get(country);

            if (languages.contains(language)) {
                translation = countryTranslations.get(country);
            }
        }

        return translation;
    }
}
