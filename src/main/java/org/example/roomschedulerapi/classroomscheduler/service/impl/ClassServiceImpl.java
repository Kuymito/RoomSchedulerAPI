package org.example.roomschedulerapi.classroomscheduler.service.impl; // Adjust package

import org.example.roomschedulerapi.classroomscheduler.model.Class;
import org.example.roomschedulerapi.classroomscheduler.model.Department;
import org.example.roomschedulerapi.classroomscheduler.model.Instructor;
import org.example.roomschedulerapi.classroomscheduler.model.Shift;
import org.example.roomschedulerapi.classroomscheduler.model.dto.*;
import org.example.roomschedulerapi.classroomscheduler.repository.ClassRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.DepartmentRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.InstructorRepository;
import org.example.roomschedulerapi.classroomscheduler.repository.ShiftRepository;
import org.example.roomschedulerapi.classroomscheduler.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;
    // Assuming you'll need these repositories to fetch related entities for create/update
    private final InstructorRepository instructorRepository;
    private final DepartmentRepository departmentRepository;
    private final ShiftRepository shiftRepository;

    @Autowired
    public ClassServiceImpl(ClassRepository classRepository,
                            InstructorRepository instructorRepository,
                            DepartmentRepository departmentRepository,
                            ShiftRepository shiftRepository) {
        this.classRepository = classRepository;
        this.instructorRepository = instructorRepository;
        this.departmentRepository = departmentRepository;
        this.shiftRepository = shiftRepository;
    }

    // Helper method to convert Class entity to ClassResponseDto
    private ClassResponseDto convertToDto(Class aClass) {
        if (aClass == null) {
            return null;
        }

        InstructorResponseDto instructorDto = null;
        if (aClass.getInstructor() != null) {
            Instructor instructor = aClass.getInstructor();
            // Assuming Role and Department on Instructor are eagerly fetched or handled by their own DTO conversion
            // For simplicity, creating a new InstructorResponseDto directly here.
            // You might have a dedicated service/mapper for Instructor to InstructorResponseDto conversion.
            String roleName = (instructor.getRole() != null) ? instructor.getRole().getRoleName() : null;
            String deptName = (instructor.getDepartment() != null) ? instructor.getDepartment().getName() : null;
            instructorDto =new InstructorResponseDto(
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
                    deptName
            );
        }

        DepartmentResponseDto departmentDto = null;
        if (aClass.getDepartment() != null) {
            Department department = aClass.getDepartment();
            departmentDto = new DepartmentResponseDto(department.getDepartmentId(), department.getName());
        }

        ShiftResponseDto shiftDto = null;
        if (aClass.getShiftEntity() != null) { // Assuming getShiftEntity() is the getter for Shift
            Shift shift = aClass.getShiftEntity();
            shiftDto = new ShiftResponseDto(
                    shift.getShiftId(),
                    shift.getName(),
                    shift.getStartTime(),
                    shift.getEndTime(),
                    shift.getScheduleType()
            );
        }

        return new ClassResponseDto(
                aClass.getClassId(),       // ID from Class entity (was courseId)
                aClass.getClassName(),   // Name from Class entity (was courseName)
                aClass.getGeneration(),
                aClass.getGroupName(),
                aClass.getMajorName(),    // Assuming these fields exist on your Class entity
                aClass.getDegreeName(),   // Assuming these fields exist on your Class entity
                aClass.isOnline(),
                aClass.isFree(),
                aClass.isIs_archived(),    // Getter for is_archived
                aClass.getCreatedAt(),
                aClass.getArchivedAt(),
                instructorDto,
                departmentDto,
                shiftDto
        );
    }


    @Override
    public List<ClassResponseDto> getAllClasses(Boolean isArchived) {
        List<Class> classes;
        if (isArchived == null) {
            classes = classRepository.findAll();
        } else {
            // You need a method like findByIs_archived in ClassRepository
            // classes = classRepository.findByIs_archived(isArchived);
            // For now, filtering in memory (less efficient for large datasets):
            List<Class> all = classRepository.findAll();
            classes = all.stream().filter(c -> c.isIs_archived() == isArchived).collect(Collectors.toList());
            // TODO: Implement proper repository method like: List<Class> findByIs_archived(boolean isArchived);
        }
        return classes.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public Optional<ClassResponseDto> getClassById(Long classId) {
        return classRepository.findById(classId).map(this::convertToDto);
    }

    @Override
    @Transactional
    public ClassResponseDto createClass(ClassCreateDto dto) {
        Class aClass = new Class();
        aClass.setClassName(dto.getClassName()); // Entity setter vs DTO getter

        // TODO: ALIGNMENT NEEDED!
        // The Class entity you aligned with DB does NOT have setCredits, setFacultyName, setSemester.
        // It also expects a Shift object for setShiftEntity, not a String.
        // You need to resolve these discrepancies.
        // For example, for shift, you'd get shiftId from DTO, fetch Shift entity, then set it.

        // Assuming 'credits', 'facultyName', 'semester', 'shift' (as String) are still in your DTO
        // but may not map directly to your current Class entity.
        // Example for 'shift' if DTO had shiftId:
        // if (dto.getShiftId() != null) { // Assuming ClassCreateDto gets a shiftId
        //     Shift shift = shiftRepository.findById(dto.getShiftId())
        //         .orElseThrow(() -> new NoSuchElementException("Shift not found with id: " + dto.getShiftId()));
        //     aClass.setShiftEntity(shift);
        // }
        // Similar logic for instructorId and departmentId if they are in ClassCreateDto

        aClass.setGeneration(dto.getGeneration());
        aClass.setGroupName(dto.getGroupName());
        // These were in your original DTO mapping but might not be in the refined Class entity
        // aClass.setMajorName(dto.getMajorName());
        // aClass.setDegreeName(dto.getDegreeName());

        // Ensure your Class entity has these setters if these fields are from DTO
        // If they are FKs like instructorId, departmentId, shiftId, fetch entities and set them.
        // Example:
        // If ClassCreateDto has Long instructorId:
        // Instructor instructor = instructorRepository.findById(dto.getInstructorId()).orElse(null);
        // aClass.setInstructor(instructor);

        // Department instructor = departmentRepository.findById(dto.getDepartmentId()).orElse(null);
        // aClass.setDepartment(department);


        aClass.setIs_archived(false);
        Class savedClass = classRepository.save(aClass);
        return convertToDto(savedClass);
    }

    @Override
    @Transactional
    public ClassResponseDto updateClass(Long classId, ClassCreateDto dto) {
        Class existingClass = classRepository.findById(classId)
                .orElseThrow(() -> new NoSuchElementException("Class not found with id: " + classId));

        existingClass.setClassName(dto.getClassName());

        // TODO: ALIGNMENT NEEDED (Same as createClass)
        // Update mapping for credits, facultyName, semester, shift, etc.
        // based on your actual Class entity structure and DTO fields.

        if (dto.getGeneration() != null) existingClass.setGeneration(dto.getGeneration());
        if (dto.getGroupName() != null) existingClass.setGroupName(dto.getGroupName());
        // ... map other fields from ClassCreateDto to existingClass ...

        Class updatedClass = classRepository.save(existingClass);
        return convertToDto(updatedClass);
    }

    @Override
    @Transactional
    public ClassResponseDto patchClass(Long classId, ClassUpdateDto dto) {
        Class aClass = classRepository.findById(classId)
                .orElseThrow(() -> new NoSuchElementException("Class not found with id: " + classId));

        if (dto.getClassName() != null) aClass.setClassName(dto.getClassName());

        // TODO: ALIGNMENT NEEDED (Same as createClass)
        // Update mapping for credits, facultyName, semester, shift, etc.
        // based on your actual Class entity structure and DTO fields.

        if (dto.getGeneration() != null) aClass.setGeneration(dto.getGeneration());
        if (dto.getGroupName() != null) aClass.setGroupName(dto.getGroupName());
        // ... map other fields from ClassUpdateDto ...

        if (dto.getIs_archived() != null) aClass.setIs_archived(dto.getIs_archived());

        Class patchedClass = classRepository.save(aClass);
        return convertToDto(patchedClass);
    }

    @Override
    @Transactional
    public ClassResponseDto archiveClass(Long classId, boolean archiveStatus) {
        Class aClass = classRepository.findById(classId)
                .orElseThrow(() -> new NoSuchElementException("Class not found with id: " + classId));
        aClass.setIs_archived(archiveStatus);
        // Potentially set/clear archivedAt timestamp here
        if (archiveStatus && aClass.getArchivedAt() == null) {
            // aClass.setArchivedAt(LocalDateTime.now()); // Assuming archivedAt setter exists
        } else if (!archiveStatus) {
            // aClass.setArchivedAt(null);
        }
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
}