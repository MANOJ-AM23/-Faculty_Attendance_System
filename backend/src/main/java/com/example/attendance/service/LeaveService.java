package com.example.attendance.service;

import com.example.attendance.model.Faculty;
import com.example.attendance.model.LeaveRequest;
import com.example.attendance.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;

    public LeaveService(LeaveRequestRepository leaveRequestRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
    }

    public LeaveRequest applyLeave(Faculty faculty,
                                   LocalDate from,
                                   LocalDate to,
                                   String reason,
                                   LocalTime startTime,
                                   LocalTime endTime) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("From and To dates are required");
        }
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("To date cannot be before From date");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Reason is required");
        }
        boolean isHalfDay = startTime != null || endTime != null;
        if (isHalfDay) {
            if (startTime == null || endTime == null) {
                throw new IllegalArgumentException("Both start time and end time are required for half-day leave");
            }
            if (!from.equals(to)) {
                throw new IllegalArgumentException("Half-day leave timing can only be used for a single date");
            }
            if (!endTime.isAfter(startTime)) {
                throw new IllegalArgumentException("End time must be after start time");
            }
        }

        LeaveRequest request = new LeaveRequest();
        request.setFaculty(faculty);
        request.setFromDate(from);
        request.setToDate(to);
        request.setReason(reason.trim());
        request.setStatus("PENDING");
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setRejectionReason(null);
        return leaveRequestRepository.save(request);
    }

    public List<LeaveRequest> getLeavesForFaculty(Faculty faculty) {
        return leaveRequestRepository.findByFaculty(faculty);
    }

    public List<LeaveRequest> getPendingLeaves() {
        return leaveRequestRepository.findByStatus("PENDING");
    }

    public List<LeaveRequest> getAllLeaves() {
        return leaveRequestRepository.findAllByOrderByIdDesc();
    }

    public Optional<LeaveRequest> getById(Long id) {
        return leaveRequestRepository.findDetailedById(id);
    }

    public LeaveRequest approveLeave(Long id) {
        LeaveRequest request = leaveRequestRepository.findDetailedById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found"));
        request.setStatus("APPROVED");
        request.setRejectionReason(null);
        leaveRequestRepository.save(request);
        return leaveRequestRepository.findDetailedById(request.getId())
                .orElseThrow(() -> new IllegalStateException("Approved leave request not found"));
    }

    public LeaveRequest rejectLeave(Long id, String rejectionReason) {
        if (rejectionReason == null || rejectionReason.isBlank()) {
            throw new IllegalArgumentException("Rejection reason is required");
        }
        LeaveRequest request = leaveRequestRepository.findDetailedById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found"));
        request.setStatus("REJECTED");
        request.setRejectionReason(rejectionReason.trim());
        leaveRequestRepository.save(request);
        return leaveRequestRepository.findDetailedById(request.getId())
                .orElseThrow(() -> new IllegalStateException("Rejected leave request not found"));
    }

    public double getApprovedLeavesCount(Faculty faculty) {
        List<LeaveRequest> approvedLeaves = leaveRequestRepository.findByFaculty(faculty)
                .stream()
                .filter(leave -> "APPROVED".equals(leave.getStatus()))
                .toList();
        return approvedLeaves.stream()
                .mapToDouble(leave -> {
                    if (leave.getStartTime() != null && leave.getEndTime() != null && leave.getFromDate().equals(leave.getToDate())) {
                        long totalMinutes = ChronoUnit.MINUTES.between(LocalTime.of(9, 0), LocalTime.of(17, 0));
                        long leaveMinutes = ChronoUnit.MINUTES.between(leave.getStartTime(), leave.getEndTime());
                        if (leaveMinutes <= 0 || totalMinutes <= 0) {
                            return 0.0;
                        }
                        return Math.min(leaveMinutes / (double) totalMinutes, 1.0);
                    }
                    long days = ChronoUnit.DAYS.between(leave.getFromDate(), leave.getToDate()) + 1;
                    return Math.max(days, 0);
                })
                .sum();
    }
}

