package com.apeixinho.bookshop.mapper;

import com.apeixinho.bookshop.entity.State;
import com.apeixinho.bookshop.model.StateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface StateMapper {

    @Mapping(target = "country", ignore = true)
    State stateDtoToState(StateDTO c);

    StateDTO stateToStateDto(State c);
    
}
