package com.example.juse.notification.mapper;

import com.example.juse.notification.dto.NotificationResponseDto;
import com.example.juse.notification.entity.Notification;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponseDto mapToDto(Notification source);

    List<NotificationResponseDto> mapToDtoListFromEntityList(List<Notification> source);

}
