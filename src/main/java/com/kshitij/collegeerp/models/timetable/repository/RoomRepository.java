package com.kshitij.collegeerp.models.timetable.repository;

import com.kshitij.collegeerp.models.timetable.entity.Room;
import com.kshitij.collegeerp.models.timetable.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByRoomNumber(String roomNumber);
    List<Room> findByRoomType(RoomType roomType);
    List<Room> findByActive(boolean active);
}