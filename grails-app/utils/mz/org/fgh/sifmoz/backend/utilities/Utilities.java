package mz.org.fgh.sifmoz.backend.utilities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import mz.org.fgh.sifmoz.backend.clinic.Clinic;
import org.grails.web.json.JSONArray;

import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Utilities {

    private static Utilities instance;

    private Utilities() {
    }

    public static Utilities getInstance(){
        if (instance == null){
            instance = new Utilities();
        }
        return instance;
    }

    public static boolean stringHasValue(String string){
        return string != null && !string.isEmpty() && string.trim().length() > 0;
    }

    public static String ensureXCaractersOnNumber(long number, int x){
        String formatedNumber = "";
        int numberOfCharacterToIncrise = 0;

        formatedNumber = number + "";

        numberOfCharacterToIncrise = x - formatedNumber.length();

        for(int i = 0; i < numberOfCharacterToIncrise; i++) formatedNumber = "0" + formatedNumber;

        return formatedNumber;
    }

    public static String concatStrings(String currentString, String toConcant, String scapeStr){
        if (!stringHasValue(currentString)) return toConcant;

        if (!stringHasValue(toConcant)) return currentString;

        return currentString + scapeStr+ toConcant;
    }

    public static boolean isStringIn(String value, String... inValues){
        if (inValues == null || value == null) return false;

        for (String str : inValues){
            if (value.equals(str)) return true;
        }

        return false;
    }

    public static boolean listHasElements(ArrayList<?> list){
        return list != null && !list.isEmpty() && list.size() > 0;
    }

    public static <T extends Object> T findOnArray(List<T> list, T toFind){
        for (T o : list) {
            if (o.equals(toFind)) return o;
        }
        return null;
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public static String generateUUID () {
        return UUID.randomUUID().toString();
    }

    public static String generateID (Clinic clinic) {
        return clinic.getCode() +"-"+generateUUID();
    }

    public static String garantirXCaracterOnNumber(long number, int x){
        String formatedNumber = "";
        int numberOfCharacterToIncrise = 0;

        formatedNumber = number + "";

        numberOfCharacterToIncrise = x - formatedNumber.length();

        for(int i = 0; i < numberOfCharacterToIncrise; i++) formatedNumber = "0" + formatedNumber;

        return formatedNumber;
    }

    public static String parseToJSON(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper.writeValueAsString(object);
    }

    public static String formatToYYYYMMDD (Date date ) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String strDate = dateFormat.format(date);

        return strDate;
    }

    public static boolean compareStringIgnoringAccents(String a, String b) {
        final Collator instance = Collator.getInstance();

        instance.setStrength(Collator.NO_DECOMPOSITION);

        if (instance.compare(a, b) == 0) return true;

        return false;
    }

    public static String stripAccents(String s)
    {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    public static String excludeProcessedProp(List<String> strings) {
        String newProps = null;
        for (String s : strings) {
            if (stringHasValue(newProps)) newProps += "."+s;
            newProps = s;
        }

        return newProps;
    }

    public List<String> splitString(String st, String separator) {
        if (!stringHasValue(st)) return null;

        List<String> result = new ArrayList<>();
        String[] exploded = st.split(separator);

        for (int i=0; i < exploded.length - 1; i++) {
            result.add(exploded[i]);
        }
        return result;
    }

}
