package com.app.bookshop.mapper;

import com.app.bookshop.entity.State;
import com.app.bookshop.model.StateDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface StateMapper {

    @Mapping(target = "country", ignore = true)
    @Mapping(target = "translations", ignore = true)
    State stateDtoToState(StateDTO c);

    @Mapping(target = "name", expression = "java(com.app.bookshop.i18n.TranslationResolver.stateName(c, lang))")
    StateDTO stateToStateDto(State c, @Context String lang);
}
