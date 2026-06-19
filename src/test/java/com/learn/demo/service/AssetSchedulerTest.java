package com.learn.demo.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.learn.demo.model.Asset;
import com.learn.demo.model.AssetAllocation;
import com.learn.demo.model.User;
import com.learn.demo.repository.AssetAllocationRepository;
import com.learn.demo.repository.NotificationRepository;
import com.learn.demo.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AssetSchedulerTest {

    @Mock
    private AssetAllocationRepository allocationRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AssetScheduler assetScheduler;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(assetScheduler, "uploadDir", "uploads");
    }

    @Test
    public void testCheckAndAlertOverdueAssets_WithUserEmail() {
        // Given
        Asset asset = new Asset();
        asset.setAssetName("Test Laptop");
        asset.setAssetCode("AST-001");

        AssetAllocation allocation = new AssetAllocation();
        allocation.setAsset(asset);
        allocation.setAssignedTo("John Doe");
        allocation.setExpectedReturnDate(LocalDate.now().minusDays(2));

        when(allocationRepository.findOverdueAllocations(any(LocalDate.class)))
            .thenReturn(Collections.singletonList(allocation));

        User user = new User();
        user.setUserName("John Doe");
        user.setUserEmail("john@example.com");

        when(userRepository.findByUserNameAndDeletedFalse("John Doe"))
            .thenReturn(Optional.of(user));

        // When
        assetScheduler.checkAndAlertOverdueAssets();

        // Then
        verify(notificationService, times(1))
            .sendOverdueNotification(eq("john@example.com"), eq("John Doe"), eq("Test Laptop"), eq("AST-001"), anyString(), eq(2L));
        verify(notificationService, never()).notifyAdmins(anyString());
    }

    @Test
    public void testCheckAndAlertOverdueAssets_FallbackToAdmins() {
        // Given
        Asset asset = new Asset();
        asset.setAssetName("Test Projector");
        asset.setAssetCode("AST-002");

        AssetAllocation allocation = new AssetAllocation();
        allocation.setAsset(asset);
        allocation.setAssignedTo("Unknown User");
        allocation.setExpectedReturnDate(LocalDate.now().minusDays(1));

        when(allocationRepository.findOverdueAllocations(any(LocalDate.class)))
            .thenReturn(Collections.singletonList(allocation));

        when(userRepository.findByUserNameAndDeletedFalse("Unknown User"))
            .thenReturn(Optional.empty());
        when(userRepository.findByUserEmailAndDeletedFalse("Unknown User"))
            .thenReturn(Optional.empty());

        // When
        assetScheduler.checkAndAlertOverdueAssets();

        // Then
        verify(notificationService, times(1)).notifyAdmins(contains("Test Projector"));
        verify(notificationService, never()).sendOverdueNotification(anyString(), anyString(), anyString(), anyString(), anyString(), anyLong());
    }
}
