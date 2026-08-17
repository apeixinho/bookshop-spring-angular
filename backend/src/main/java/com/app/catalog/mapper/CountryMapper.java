package com.app.catalog.mapper;

import com.app.catalog.entity.Country;
import com.app.catalog.model.CountryDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CountryMapper {

    @Mapping(target = "states", ignore = true)
    @Mapping(target = "translations", ignore = true)
    Country countryDtoToCountry(CountryDTO c);

    @Mapping(target = "name", expression = "java(com.app.catalog.i18n.TranslationResolver.countryName(c, lang))")
    CountryDTO countryToCountryDto(Country c, @Context String lang);
}
