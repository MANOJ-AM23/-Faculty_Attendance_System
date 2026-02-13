package com.example.attendance.service;

import com.example.attendance.model.Faculty;
import com.example.attendance.model.LeaveRequest;
import com.example.attendance.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;

    public LeaveService(LeaveRequestRepository leaveRequestRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
    }

    public LeaveRequest applyLeave(Faculty faculty, LocalDate from, LocalDate to, String reason) {
        LeaveRequest request = new LeaveRequest();
        request.setFaculty(faculty);
        request.setFromDate(from);
        request.setToDate(to);
        request.setReason(reason);
        request.setStatus("PENDING");
        return leaveRequestRepository.save(request);
    }

    public List<LeaveRequest> getLeavesForFaculty(Faculty faculty) {
        return leaveRequestRepository.findByFaculty(faculty);
    }

    public List<LeaveRequest> getPendingLeaves() {
        return leaveRequestRepository.findByStatus("PENDING");
    }

    public Optional<LeaveRequest> getById(Long id) {
        return leaveRequestRepository.findById(id);
    }

    public LeaveRequest approveLeave(Long id) {
        LeaveRequest request = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found"));
        request.setStatus("APPROVED");
        return leaveRequestRepository.save(request);
    }

    public LeaveRequest rejectLeave(Long id) {
        LeaveRequest request = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found"));
        request.setStatus("REJECTED");
        return leaveRequestRepository.save(request);
    }
}

