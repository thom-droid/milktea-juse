package com.example.juse.tag.mapper;

import com.example.juse.tag.entity.BoardTag;
import com.example.juse.tag.entity.UserTag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {

    @Mapping(target = "tag.name", source = "tag")
    UserTag toUserTagFrom(String tag);

    @Mapping(target = "tag.name", source = "tag")
    BoardTag toBoardTagFrom(String tag);
}
