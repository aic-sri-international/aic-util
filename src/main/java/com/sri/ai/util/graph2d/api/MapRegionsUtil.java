package com.sri.ai.util.graph2d.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class MapRegionsUtil {
  private static final String[] SOUTH_SUDAN_STATES =
      {"Central Equatoria","Eastern Equatoria","Jonglei","Lakes","Northern Bahr el Ghazal","Unity",
          "Upper Nile","Warrap","Western Bahr el Ghazal","Western Equatoria"};

  private static final String[] SOUTH_SUDAN_COUNTIES =
      {"Abiemnhom","Abyei","Akobo","Aweil Centre","Aweil East","Aweil North","Aweil South",
          "Aweil West","Awerial","Ayod","Baliet","Bor South","Budi","Canal/Pigi","Cueibet","Duk","Ezo",
          "Fangak","Fashoda","Gogrial East","Gogrial West","Guit","Ibba","Ikotos","Juba","Jur River","Kajo-Keji",
          "Kapoeta East","Kapoeta North","Kapoeta South","Koch","Lainya","Leer","Longochuk","Lopa/Lafon","Luakpiny/Nasir",
          "Maban","Magwi","Maiwut","Malakal","Manyo","Maridi","Mayendit","Mayom","Melut","Morobo","Mundri East","Mundri West",
          "Mvolo","Nagero","Nyirol","Nzara","Panyijiar","Panyikang","Pariang","Pibor","Pochalla","Raga","Renk","Rubkona",
          "Rumbek Centre","Rumbek East","Rumbek North","Tambura","Terekeka","Tonj East","Tonj North","Tonj South","Torit",
          "Twic","Twic East","Ulang","Uror","Wau","Wulu","Yambio","Yei","Yirol East","Yirol West"};

  private static final Names STATES;
  private static final Names COUNTIES;
  private static final Pattern INVALID_CHARACTERS_PATTERN = Pattern.compile("[^a-zA-Z0-9_']");

  public static class Names {
    private Set<String> convertedNames;
    private Map<String, String> newNameToOriginalNames;

    Names(Set<String> convertedNames,
        Map<String, String> newNameToOriginalNames) {
      this.convertedNames = Collections.unmodifiableSet(convertedNames);
      this.newNameToOriginalNames = Collections.unmodifiableMap(newNameToOriginalNames);
    }

    public Set<String> getConvertedNames() {
      return convertedNames;
    }

    public Map<String, String> getNewNameToOriginalNames() {
      return newNameToOriginalNames;
    }
  }

  private static Names convert(String[] names) {
    Set<String> convertedNames = new LinkedHashSet<>();
    Map<String, String> newNameToOriginalNames = new HashMap<>();

    for (String name : names) {
      String newName = INVALID_CHARACTERS_PATTERN.matcher(name).replaceAll("");
      convertedNames.add(newName);
      newNameToOriginalNames.put(newName, name);
    }

    return new Names(convertedNames, newNameToOriginalNames);
  }

  static {
    STATES = convert(SOUTH_SUDAN_STATES);
    COUNTIES = convert(SOUTH_SUDAN_COUNTIES);
  }

  public static boolean isState(String value) {
    return STATES.getConvertedNames().contains(value);
  }

  public static boolean isCounty(String value) {
    return COUNTIES.getConvertedNames().contains(value);
  }

  public static boolean isStateOrCounty(String value) {
    return isState(value) || isCounty(value);
  }

  public static String toOriginalStateName(String newName) {
    return STATES.newNameToOriginalNames.get(newName);
  }

  public static Names getStatesNames() {
    return STATES;
  }

  public static Names getCountyNames() {
    return COUNTIES;
  }
}
