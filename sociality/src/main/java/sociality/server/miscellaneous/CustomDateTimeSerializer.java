package sociality.server.miscellaneous;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

public class CustomDateTimeSerializer extends StdScalarSerializer<LocalDateTime> {

	private static final long serialVersionUID = -8773296302118088321L;
	private final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	protected CustomDateTimeSerializer() {
		super(LocalDateTime.class);
	}
	
	@Override
	public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		 gen.writeStartObject();
         gen.writeStringField("date", DATE_FORMAT.format(value));
         gen.writeEndObject();
		
	}

}
