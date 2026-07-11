package com.kshitij.collegeerp.models.timetable.service;

import com.kshitij.collegeerp.common.exception.ResourceNotFoundException;
import com.kshitij.collegeerp.models.timetable.dto.RoomRequest;
import com.kshitij.collegeerp.models.timetable.dto.RoomResponse;
import com.kshitij.collegeerp.models.timetable.entity.Room;
import com.kshitij.collegeerp.models.timetable.repository.RoomRepository;
import com.kshitij.collegeerp.models.timetable.repository.TimetableEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final TimetableEntryRepository timetableEntryRepository;

    // =========================================================
    // Create Room
    // =========================================================

    @Transactional
    public RoomResponse create(RoomRequest request) {

        validateDuplicateRoom(request.getRoomNumber());

        Room room = Room.builder()
                .roomNumber(request.getRoomNumber().trim())
                .capacity(request.getCapacity())
                .roomType(request.getRoomType())
                .active(true)
                .build();

        return mapToResponse(roomRepository.save(room));
    }

    // =========================================================
    // Update Room
    // =========================================================

    @Transactional
    public RoomResponse update(Long id, RoomRequest request) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room not found with id: " + id));

        if (!room.getRoomNumber().equalsIgnoreCase(request.getRoomNumber())
                && roomRepository.existsByRoomNumber(request.getRoomNumber())) {

            throw new RuntimeException(
                    "Room already exists with number: "
                            + request.getRoomNumber());
        }

        room.setRoomNumber(request.getRoomNumber().trim());
        room.setCapacity(request.getCapacity());
        room.setRoomType(request.getRoomType());

        return mapToResponse(roomRepository.save(room));
    }

    // =========================================================
    // Get Room By Id
    // =========================================================

    public RoomResponse getById(Long id) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room not found with id: " + id));

        return mapToResponse(room);
    }

    // =========================================================
    // Get All Rooms
    // =========================================================

    public List<RoomResponse> getAll() {

        return roomRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================================================
    // Delete Room
    // =========================================================

    @Transactional
    public void delete(Long id) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room not found with id: " + id));

        if (timetableEntryRepository.existsByRoomId(id)) {
            throw new RuntimeException(
                    "Cannot delete room because it is assigned to the timetable."
            );
        }

        roomRepository.delete(room);
    }

    // =========================================================
    // Activate Room
    // =========================================================

    @Transactional
    public RoomResponse activate(Long id) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room not found with id: " + id));

        room.setActive(true);

        return mapToResponse(roomRepository.save(room));
    }

    // =========================================================
    // Deactivate Room
    // =========================================================

    @Transactional
    public RoomResponse deactivate(Long id) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room not found with id: " + id));

        if (timetableEntryRepository.existsByRoomId(id)) {
            throw new RuntimeException(
                    "Cannot deactivate room because it is being used in the timetable."
            );
        }

        room.setActive(false);

        return mapToResponse(roomRepository.save(room));
    }

    // =========================================================
    // Validation
    // =========================================================

    private void validateDuplicateRoom(String roomNumber) {

        if (roomRepository.existsByRoomNumber(roomNumber.trim())) {

            throw new RuntimeException(
                    "Room already exists with number: " + roomNumber
            );
        }
    }

    // =========================================================
    // Mapper
    // =========================================================

    private RoomResponse mapToResponse(Room room) {

        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .capacity(room.getCapacity())
                .roomType(room.getRoomType())
                .active(room.isActive())
                .build();
    }

}