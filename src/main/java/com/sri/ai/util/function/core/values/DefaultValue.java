package com.sri.ai.util.function.core.values;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.sri.ai.util.function.api.values.Value;

public class DefaultValue implements Value {
  private final Object value;

  public DefaultValue(Object value) {
    this.value = Validate.notNull(value, "value cannot be null");
  }

  @Override
  public Object objectValue() {
    return value;
  }

  @Override
  public String stringValue() {
    return value.toString();
  }

  @Override
  public int intValue() {
    Integer result = toNumber(Integer.class);
    return result;
  }

  @Override
  public double doubleValue() {
    Double result = toNumber(Double.class);
    return result;
  }

  @SuppressWarnings("unchecked")
  private <T extends Number> T toNumber(Class<T> classType) {
    T result;

    if (classType.equals(value.getClass()) ) {
      result = (T) value;
    } else {
      String trimmedStringValue = StringUtils.trimToEmpty(value.toString());

      try {
        Validate.notEmpty(trimmedStringValue,
            "value cannot be converted to a number because its string value is blank");
        Double number = Double.valueOf(trimmedStringValue);
        if (classType.equals(Double.class)) {
          result = (T) number;
        } else {
          result = (T) new Integer(number.intValue());
        }
      } catch(NumberFormatException e) {
        String message
            = String.format("The value is an instance of %s and its trimmed string value is '%s' "
            + " which cannot be converted into a Double",
            value.getClass().getSimpleName(), trimmedStringValue);

        throw new NumberFormatException(message) ;
      }
    }

    return result;
  }

  @Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((value == null) ? 0 : value.hashCode());
	return result;
}

@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	DefaultValue other = (DefaultValue) obj;
	if (value == null) {
		if (other.value != null)
			return false;
	} else if (!value.equals(other.value))
		return false;
	return true;
}

@Override
  public String toString() {
    return value.toString();
  }
}
