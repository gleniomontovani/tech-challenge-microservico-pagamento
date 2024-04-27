package br.com.postech.techchallenge.microservico.pagamento.configuration;

import java.util.function.Supplier;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;

class NullableMapper extends ModelMapper{

	@Override
	public <D> D map(Object source, Class<D> destinationType) {
		if(source == null) {
			return null;
		}
		return super.map(source, destinationType);
	}
}

@Provider
public class ModelMapperConfiguration implements ContextResolver<ModelMapper> {

	@Override
	public ModelMapper getContext(Class<?> type) {
		return getModelMapper();
	}

	public static ModelMapper getModelMapper() {
		ModelMapper modelMapper = new NullableMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		modelMapper.getConfiguration().setAmbiguityIgnored(true);
		modelMapper.getConfiguration().setFieldMatchingEnabled(true);
		modelMapper.getConfiguration().setFieldAccessLevel(AccessLevel.PRIVATE);
		modelMapper.addConverter(converterStringToString());
	
		return modelMapper;
	}
	
	private static Converter<String, String> converterStringToString() {
		return new AbstractConverter<String, String>() {
			protected String convert(String source) {
				return source == null ? null : source.trim();
			}
		};
	}
	
	public static <S, D> Converter <S, D> converterWithDestinationSupplier(Supplier<? extends D> supplier) {
	    return ctx -> ctx.getMappingEngine().map(ctx.create(ctx.getSource(), supplier.get()));
	}
}
