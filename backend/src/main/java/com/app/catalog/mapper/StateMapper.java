package com.app.catalog.mapper;

import com.app.catalog.entity.State;
import com.app.catalog.model.StateDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface StateMapper {

    @Mapping(target = "country", ignore = true)
    @Mapping(target = "translations", ignore = true)
    State stateDtoToState(StateDTO c);

    @Mapping(target = "name", expression = "java(com.app.catalog.i18n.TranslationResolver.stateName(c, lang))")
    StateDTO stateToStateDto(State c, @Context String lang);
}
