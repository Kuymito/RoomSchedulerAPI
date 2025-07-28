package org.example.roomschedulerapi.classroomscheduler.service.impl;

import org.example.roomschedulerapi.classroomscheduler.exception.DuplicateResourceException;
import org.example.roomschedulerapi.classroomscheduler.exception.InstructorConflictException; // Import the custom exception
import org.example.roomschedulerapi.classroomscheduler.model.*;
import org.example.roomschedulerapi.classroomscheduler.model.Class; // Explicit import
import org.example.roomschedulerapi.classroomscheduler.model.dto.*;
import org.example.roomschedulerapi.classroomscheduler.repository.*;
import org.example.roomschedulerapi.classroomscheduler.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;
    private final InstructorRepository instructorRepository;
    private final DepartmentRepository departmentRepository;
    private final ShiftRepository shiftRepository;
    private final ClassInstructorRepository classInstructorRepository;
    private final ScheduleRepository scheduleRepository;

    @Autowired
    public ClassServiceImpl(ClassRepository classRepository, InstructorRepository instructorRepository, DepartmentRepository departmentRepository, ShiftRepository shiftRepository, ClassInstructorRepository classInstructorRepository, ScheduleRepository scheduleRepository) {
        this.classRepository = classRepository;
        this.instructorRepository = instructorRepository;
        this.departmentRepository = departmentRepository;
        this.shiftRepository = shiftRepository;
        this.classInstructorRepository = classInstructorRepository;
        this.scheduleRepository = scheduleRepository;
    }

    /**
     * Converts a Class entity to a ClassResponseDto.
     *
     * @param aClass The Class entity.
     * @return The corresponding DTO.
     */
    private ClassResponseDto convertToDto(Class aClass) {
        if (aClass == null) {
            return null;
        }

        if (aClass.getClassInstructors() == null) {
            aClass.setClassInstructors(new java.util.ArrayList<>());
        }

        Map<String, ClassDayDetailsDto> dailyScheduleMap = aClass.getClassInstructors().stream()
                .collect(Collectors.toMap(
                        ci -> ci.getDayOfWeek().name(),
                        ci -> {
                            Instructor instructor = ci.getInstructor();
                            String roleName = (instructor.getRole() != null) ? instructor.getRole().getRoleName() : null;

                            // Safely get department details
                            Long deptId = (instructor.getDepartment() != null) ? instructor.getDepartment().getDepartmentId() : null;
                            String deptName = (instructor.getDepartment() != null) ? instructor.getDepartment().getName() : null;

                            InstructorResponseDto instructorDto = new InstructorResponseDto(
                                    instructor.getInstructorId(),
                                    instructor.getFirstName(),
                                    instructor.getLastName(),
                                    instructor.getEmail(),
                                    instructor.getPhone(),
                                    instructor.getDegree(),
                                    instructor.getMajor(),
                                    instructor.getProfile(),
                                    instructor.getAddress(),
                                    instructor.isArchived(),
                                    roleName,
                                    deptId, // Correctly pass the department ID
                                    deptName // Correctly pass the department name
                            );

                            return new ClassDayDetailsDto(ci.isOnline(), instructorDto);
                        },
                        (existing, replacement) -> existing // Added a merge function for safety
                ));

        // Create DTOs for related entities
        DepartmentResponseDto departmentDto = (aClass.getDepartment() != null) ?
                new DepartmentResponseDto(aClass.getDepartment().getDepartmentId(), aClass.getDepartment().getName()) : null;
        ShiftResponseDto shiftDto = (aClass.getShift() != null) ?
                new ShiftResponseDto(aClass.getShift().getShiftId(), aClass.getShift().getStartTime(), aClass.getShift().getEndTime()) : null;


        return new ClassResponseDto(aClass.getClassId(), aClass.getClassName(), aClass.getGeneration(), aClass.getGroupName(), aClass.getMajorName(),
                aClass.getDegreeName(), aClass.getSemester(), aClass.isFree(), aClass.isArchived(),
                aClass.getCreatedAt(), aClass.getArchivedAt(), dailyScheduleMap, departmentDto, shiftDto);
    }

    @Override
    public List<ClassResponseDto> getAllClasses(Boolean isArchived) {
        List<Class> classes = isArchived == null ? classRepository.findAll() : classRepository.findByIsArchived(isArchived);
        return classes.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public Optional<ClassResponseDto> getClassById(Long classId) {
        return classRepository.findById(classId).map(this::convertToDto);
    }

    @Override
    @Transactional
    public ClassResponseDto createClass(ClassCreateDto dto) {
        // --- DUPLICATE CHECK ---
        // Check if a class with the same generation, group name, and major already exists.
        Optional<Class> existingClass = classRepository.findByGenerationAndGroupNameAndMajorName(
                dto.getGeneration(),
                dto.getGroupName(),
                dto.getMajor()
        );
        if (classRepository.existsByClassName(dto.getClassName())) {
            throw new DuplicateResourceException("A class with the name '" + dto.getClassName() + "' already exists.");
        }

        checkForDuplicateClass(dto, null);


        if (existingClass.isPresent()) {
            throw new DataIntegrityViolationException(
                    "A class with the same generation (" + dto.getGeneration() +
                            "), group (" + dto.getGroupName() +
                            "), and major (" + dto.getMajor() + ") already exists."
            );
        }
        // --- END OF DUPLICATE CHECK ---

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new NoSuchElementException("Department not found with id: " + dto.getDepartmentId()));
        Shift shift = shiftRepository.findById(dto.getShiftId())
                .orElseThrow(() -> new NoSuchElementException("Shift not found with id: " + dto.getShiftId()));

        Class aClass = new Class();
        aClass.setClassName(dto.getClassName());
        aClass.setGeneration(dto.getGeneration());
        aClass.setGroupName(dto.getGroupName());
        aClass.setMajorName(dto.getMajor());
        aClass.setDegreeName(dto.getDegree());
        aClass.setSemester(dto.getSemester());
        aClass.setYear(dto.getYear());
        aClass.setDepartment(department);
        aClass.setShift(shift);
        aClass.setArchived(false);

        Class savedClass = classRepository.save(aClass);
        return convertToDto(savedClass);
    }

    @Override
    @Transactional
    public ClassResponseDto updateClass(Long classId, ClassCreateDto dto) {
        Class existingClass = classRepository.findById(classId)
                .orElseThrow(() -> new NoSuchElementException("Class not found with id: " + classId));

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new NoSuchElementException("Department not found with id: " + dto.getDepartmentId()));
        Shift shift = shiftRepository.findById(dto.getShiftId())
                .orElseThrow(() -> new NoSuchElementException("Shift not found with id: " + dto.getShiftId()));

        existingClass.setClassName(dto.getClassName());
        existingClass.setGeneration(dto.getGeneration());
        existingClass.setGroupName(dto.getGroupName());
        existingClass.setMajorName(dto.getMajor());
        existingClass.setDegreeName(dto.getDegree());
        existingClass.setSemester(dto.getSemester());
        existingClass.setYear(dto.getYear());
        existingClass.setDepartment(department);
        existingClass.setShift(shift);

        Class updatedClass = classRepository.save(existingClass);
        return convertToDto(updatedClass);
    }

    @Override
    public List<ClassResponseDto> getClassesByExpirationStatus(boolean isExpired) {
        List<Class> classes = classRepository.findByIsExpired(isExpired);
        return classes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public ClassResponseDto patchClass(Long classId, ClassUpdateDto dto) {
        Class aClass = classRepository.findById(classId)
                .orElseThrow(() -> new NoSuchElementException("Class not found with id: " + classId));

        if (dto.getClassName() != null) aClass.setClassName(dto.getClassName());
        if (dto.getGeneration() != null) aClass.setGeneration(dto.getGeneration());
        if (dto.getGroupName() != null) aClass.setGroupName(dto.getGroupName());
        if (dto.getMajor() != null) aClass.setMajorName(dto.getMajor());
        if (dto.getDegree() != null) aClass.setDegreeName(dto.getDegree());
        if (dto.getSemester() != null) aClass.setSemester(dto.getSemester());
        if (dto.getYear() != null) aClass.setYear(dto.getYear());
        if (dto.getIsArchived() != null) aClass.setArchived(dto.getIsArchived());

        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId()).orElseThrow(() -> new NoSuchElementException("Department not found"));
            aClass.setDepartment(department);
        }
        if (dto.getShiftId() != null && !dto.getShiftId().equals(aClass.getShift().getShiftId())) {
            Shift newShift = shiftRepository.findById(dto.getShiftId())
                    .orElseThrow(() -> new NoSuchElementException("Shift not found with id: " + dto.getShiftId()));

            // Perform instructor conflict check
            for (ClassInstructor ci : aClass.getClassInstructors()) {
                checkForInstructorConflict(ci.getInstructor(), ci.getDayOfWeek(), newShift, aClass.getClassId());
            }

            // Update the shift
            aClass.setShift(newShift);

            // ✨ 4. NEW: Delete all existing schedule entries for this class.
            // This is the safest way to prevent room conflicts when a shift changes.
            scheduleRepository.deleteAllByAClass(aClass);

            // Handle un-assigning instructors based on the new shift type
            List<ClassInstructor> instructorsToRemove = new ArrayList<>();
            if ("Weekend".equalsIgnoreCase(newShift.getScheduleType())) {
                instructorsToRemove = aClass.getClassInstructors().stream()
                        .filter(ci -> ci.getDayOfWeek() != DaysOfWeek.SATURDAY && ci.getDayOfWeek() != DaysOfWeek.SUNDAY)
                        .collect(Collectors.toList());
            } else if ("Weekday".equalsIgnoreCase(newShift.getScheduleType())) {
                instructorsToRemove = aClass.getClassInstructors().stream()
                        .filter(ci -> ci.getDayOfWeek() == DaysOfWeek.SATURDAY || ci.getDayOfWeek() == DaysOfWeek.SUNDAY)
                        .collect(Collectors.toList());
            }

            if (!instructorsToRemove.isEmpty()) {
                aClass.getClassInstructors().removeAll(instructorsToRemove);
                classInstructorRepository.deleteAll(instructorsToRemove);
            }
        }

        Class patchedClass = classRepository.save(aClass);
        return convertToDto(patchedClass);
    }

    private void checkForInstructorConflict(Instructor instructor, DaysOfWeek day, Shift shift, Long classToExcludeId) {
        // This helper method is still recommended to avoid duplicate logic
        List<ClassInstructor> instructorAssignments = classInstructorRepository.findByInstructor(instructor);
        for (ClassInstructor assignment : instructorAssignments) {
            if (!assignment.getAClass().getClassId().equals(classToExcludeId) &&
                    assignment.getDayOfWeek().equals(day) &&
                    assignment.getAClass().getShift().getShiftId().equals(shift.getShiftId())) {
                throw new InstructorConflictException(
                        "Conflict: Instructor " + instructor.getFirstName() + " is already scheduled for another class at this time."
                );
            }
        }
    }

    @Override
    @Transactional
    public ClassResponseDto archiveClass(Long classId, boolean archiveStatus) {
        Class aClass = classRepository.findById(classId)
                .orElseThrow(() -> new NoSuchElementException("Class not found with id: " + classId));
        aClass.setArchived(archiveStatus);
        aClass.setArchivedAt(archiveStatus ? LocalDateTime.now() : null);
        Class savedClass = classRepository.save(aClass);
        return convertToDto(savedClass);
    }

    @Override
    @Transactional
    public void deleteClass(Long classId) {
        if (!classRepository.existsById(classId)) {
            throw new NoSuchElementException("Class not found with id: " + classId);
        }
        classRepository.deleteById(classId);
    }

    @Transactional
    @Override
    public ClassResponseDto assignInstructor(AssignInstructorDto assignInstructorDto) {
        // 1. Fetch the required entities
        Class existingClass = classRepository.findById(assignInstructorDto.getClassId())
                .orElseThrow(() -> new NoSuchElementException("Class not found with id: " + assignInstructorDto.getClassId()));
        Instructor instructorToAssign = instructorRepository.findById(assignInstructorDto.getInstructorId())
                .orElseThrow(() -> new NoSuchElementException("Instructor not found with id: " + assignInstructorDto.getInstructorId()));

        // Check if the instructor is archived
        if (instructorToAssign.isArchived()) {
            throw new IllegalArgumentException("Cannot assign an archived instructor.");
        }

        DaysOfWeek dayToAssign = DaysOfWeek.valueOf(assignInstructorDto.getDayOfWeek().toUpperCase());
        Shift classShift = existingClass.getShift();

        // --- CONFLICT DETECTION LOGIC ---
        // Find all other classes this instructor is assigned to.
        List<ClassInstructor> instructorAssignments = classInstructorRepository.findByInstructor(instructorToAssign);

        for (ClassInstructor assignment : instructorAssignments) {
            // Check if the assignment is on the same day and in the same shift
            boolean isSameDay = assignment.getDayOfWeek().equals(dayToAssign);
            boolean isSameShift = assignment.getAClass().getShift().getShiftId().equals(classShift.getShiftId());
            // Make sure we are not comparing the class to itself
            boolean isDifferentClass = !assignment.getAClass().getClassId().equals(existingClass.getClassId());

            if (isSameDay && isSameShift && isDifferentClass) {
                // If a conflict is found, throw the custom exception.
                throw new InstructorConflictException(
                        "Conflict: Instructor " + instructorToAssign.getFirstName() + " " + instructorToAssign.getLastName() +
                                " is already scheduled for another class (" + assignment.getAClass().getClassName() +
                                ") at this time."
                );
            }
        }
        // --- END OF CONFLICT DETECTION ---


        // Check for existing assignment on this day for this specific class
        Optional<ClassInstructor> existingAssignment = existingClass.getClassInstructors().stream()
                .filter(ci -> ci.getDayOfWeek().equals(dayToAssign))
                .findFirst();

        if (existingAssignment.isPresent()) {
            ClassInstructor currentAssignment = existingAssignment.get();
            // If the day is already assigned, but to a different instructor, it's a conflict within the class itself.
            if (!currentAssignment.getInstructor().equals(instructorToAssign)) {
                throw new InstructorConflictException(
                        "Day " + dayToAssign + " is already assigned to another instructor for this class."
                );
            }
            // If it's the same instructor, just update the online status.
            currentAssignment.setOnline(assignInstructorDto.isOnline());
            classInstructorRepository.save(currentAssignment);
        } else {
            // If no assignment exists for this day, create a new one.
            ClassInstructor newAssignment = new ClassInstructor();
            newAssignment.setAClass(existingClass);
            newAssignment.setInstructor(instructorToAssign);
            newAssignment.setDayOfWeek(dayToAssign);
            newAssignment.setOnline(assignInstructorDto.isOnline());

            existingClass.getClassInstructors().add(newAssignment);
            classInstructorRepository.save(newAssignment);
        }

        // Return the updated class DTO
        return convertToDto(classRepository.save(existingClass));
    }


    @Override
    @Transactional
    public ClassResponseDto unassignInstructor(UnassignInstructorDto unassignInstructorDto) {
        Class existingClass = classRepository.findById(unassignInstructorDto.getClassId())
                .orElseThrow(() -> new NoSuchElementException("Class not found with id: " + unassignInstructorDto.getClassId()));

        DaysOfWeek day = DaysOfWeek.valueOf(unassignInstructorDto.getDayOfWeek().toUpperCase());

        // Find the specific assignment to remove
        Optional<ClassInstructor> assignmentToRemove = existingClass.getClassInstructors().stream()
                .filter(ci -> ci.getDayOfWeek().equals(day))
                .findFirst();

        if (assignmentToRemove.isPresent()) {
            ClassInstructor classInstructor = assignmentToRemove.get();
            existingClass.getClassInstructors().remove(classInstructor);
            classInstructorRepository.delete(classInstructor);
        } else {
            throw new NoSuchElementException("No instructor assigned to this class on " + day);
        }

        return convertToDto(existingClass);
    }


    @Override
    public List<ClassResponseDto> getClassesForAuthenticatedInstructor(String instructorEmail) {
        Instructor instructor = instructorRepository.findByEmail(instructorEmail)
                .orElseThrow(() -> new NoSuchElementException("Instructor not found with email: " + instructorEmail));

        List<ClassInstructor> assignments = classInstructorRepository.findByInstructor(instructor);

        List<Class> classes = assignments.stream()
                .map(ClassInstructor::getAClass)
                .filter(aClass -> !aClass.isArchived())
                .distinct()
                .collect(Collectors.toList());

        return classes.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private void checkForDuplicateClass(ClassCreateDto dto, Long classIdToExclude) {
        Optional<Class> existingClass = classRepository.findByGenerationAndGroupNameAndMajorName(
                dto.getGeneration(),
                dto.getGroupName(),
                dto.getMajor()
        );

        if (existingClass.isPresent() && !existingClass.get().getClassId().equals(classIdToExclude)) {
            throw new DuplicateResourceException(
                    "A class with the same generation, group, and major already exists."
            );
        }
    }

    @Override
    @Transactional
    public ClassResponseDto swapInstructorsInClass(SwapInstructorsInClassDto dto) {
        if (dto.getFromDayOfWeek().equalsIgnoreCase(dto.getToDayOfWeek())) {
            throw new IllegalArgumentException("Cannot swap an assignment with itself.");
        }

        DaysOfWeek fromDay = DaysOfWeek.valueOf(dto.getFromDayOfWeek().toUpperCase());
        DaysOfWeek toDay = DaysOfWeek.valueOf(dto.getToDayOfWeek().toUpperCase());

        // Use Optional to handle cases where a day might be unassigned
        Optional<ClassInstructor> fromAssignmentOpt = classInstructorRepository.findByaClass_ClassIdAndDayOfWeek(dto.getClassId(), fromDay);
        Optional<ClassInstructor> toAssignmentOpt = classInstructorRepository.findByaClass_ClassIdAndDayOfWeek(dto.getClassId(), toDay);

        // Case 1: Swapping two assigned instructors
        if (fromAssignmentOpt.isPresent() && toAssignmentOpt.isPresent()) {
            ClassInstructor fromAssignment = fromAssignmentOpt.get();
            ClassInstructor toAssignment = toAssignmentOpt.get();

            Instructor tempInstructor = fromAssignment.getInstructor();
            fromAssignment.setInstructor(toAssignment.getInstructor());
            toAssignment.setInstructor(tempInstructor);

            classInstructorRepository.save(fromAssignment);
            classInstructorRepository.save(toAssignment);
        }
        // Case 2: Moving an assigned instructor to an unassigned day
        else if (fromAssignmentOpt.isPresent() && toAssignmentOpt.isEmpty()) {
            ClassInstructor fromAssignment = fromAssignmentOpt.get();
            fromAssignment.setDayOfWeek(toDay); // Move the assignment to the new day
            classInstructorRepository.save(fromAssignment);
        }
        // Case 3: Moving an instructor from an unassigned day to an assigned day
        else if (fromAssignmentOpt.isEmpty() && toAssignmentOpt.isPresent()) {
            ClassInstructor toAssignment = toAssignmentOpt.get();
            toAssignment.setDayOfWeek(fromDay); // Move the assignment to the new day
            classInstructorRepository.save(toAssignment);
        }
        // Case 4: Both days are unassigned
        else {
            throw new NoSuchElementException("Cannot perform swap: both days are unassigned.");
        }

        // Fetch and return the updated class details
        Class updatedClass = classRepository.findById(dto.getClassId())
                .orElseThrow(() -> new NoSuchElementException("Class not found with id: " + dto.getClassId()));
        return convertToDto(updatedClass);
    }

    /**
     * Helper method to find a ClassInstructor assignment.
     */
    private ClassInstructor findClassInstructor(Long classId, String day) {
        DaysOfWeek dayOfWeek = DaysOfWeek.valueOf(day.toUpperCase());
        return classInstructorRepository.findByaClass_ClassIdAndDayOfWeek(classId, dayOfWeek)
                .orElseThrow(() -> new NoSuchElementException("No assignment found for class " + classId + " on " + day));
    }

    @Override
    @Transactional
    public ClassResponseDto replaceInstructor(ReplaceInstructorDto dto) {
        // 1. Find the existing assignment that needs to be changed.
        DaysOfWeek day = DaysOfWeek.valueOf(dto.getDayOfWeek().toUpperCase());
        ClassInstructor assignmentToUpdate = classInstructorRepository
                .findByaClass_ClassIdAndDayOfWeek(dto.getClassId(), day)
                .orElseThrow(() -> new NoSuchElementException(
                        "No instructor is assigned to this class on " + dto.getDayOfWeek()
                ));

        // 2. Find the new instructor to be assigned.
        Instructor newInstructor = instructorRepository.findById(dto.getNewInstructorId())
                .orElseThrow(() -> new NoSuchElementException(
                        "The new instructor with ID " + dto.getNewInstructorId() + " was not found."
                ));

        // 3. Perform validations on the new instructor.
        if (newInstructor.isArchived()) {
            throw new IllegalArgumentException("Cannot assign an archived instructor.");
        }

        // Prevent assigning the same instructor.
        if (assignmentToUpdate.getInstructor().getInstructorId().equals(newInstructor.getInstructorId())) {
            throw new IllegalArgumentException("The new instructor is already assigned to this day.");
        }

        // Check if the new instructor has any time conflicts.
        checkForInstructorConflict(
                newInstructor,
                assignmentToUpdate.getDayOfWeek(),
                assignmentToUpdate.getAClass().getShift(),
                null // Pass null because we are checking for conflicts across ALL classes.
        );

        // 4. Replace the instructor and save the change.
        assignmentToUpdate.setInstructor(newInstructor);
        classInstructorRepository.save(assignmentToUpdate);

        // 5. Return the updated class details.
        Class updatedClass = classRepository.findById(dto.getClassId())
                .orElseThrow(() -> new NoSuchElementException("Class not found with id: " + dto.getClassId()));
        return convertToDto(updatedClass);
    }
}
