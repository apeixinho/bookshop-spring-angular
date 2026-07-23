package com.apeixinho.bookshop.mapper;

import com.apeixinho.bookshop.entity.Country;
import com.apeixinho.bookshop.model.CountryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CountryMapper {

    @Mapping(target = "states", ignore = true)
    Country countryDtoToCountry(CountryDTO c);

    CountryDTO countryToCountryDto(Country c);
}
