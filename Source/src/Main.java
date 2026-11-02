import java.util.*;

class Course {
   private static int auto_id = 0;
   private int id;
   private String title, description;
   private List<Module> modules;

   Course(String title, String description) {
      this.id = auto_id += 1;
      this.title = title;
      this.description = description;
      modules = new ArrayList<>();
   }

   void addModule(Module M) {
      this.modules.add(M);
   }

   List<Module> getModules() {
      return this.modules;
   }

   Set<Lesson> getTotalLessons() {
      Set<Lesson> lessons = new HashSet<>();
      for (Module M : this.getModules()) {
         lessons.addAll(M.getLessons());
      }
      return lessons;
   }

   public int getID() {
      return this.id;
   }
}

class Module {
   private static int auto_id = 0;
   private int id;
   private String title;
   private List<Lesson> lessons;

   Module(String title) {
      this.id = auto_id += 1;
      this.title = title;
      this.lessons = new ArrayList<>();
   }

   void addLesson(Lesson l) {
      this.lessons.add(l);
   }

   List<Lesson> getLessons() {
      return this.lessons;
   }
}

class Lesson {
   private static int auto_id = 0;
   private int id, durationInMinutes;
   private String title, contentUrl;

   Lesson(int durationInMinutes, String title, String contentUrl) {
      this.id = auto_id += 1;
      this.durationInMinutes = durationInMinutes;
      this.title = title;
      this.contentUrl = contentUrl;
   }

   @Override
   public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) return false;
      Lesson lesson = (Lesson) o;
      return id == lesson.id && durationInMinutes == lesson.durationInMinutes && Objects.equals(title, lesson.title) && Objects.equals(contentUrl, lesson.contentUrl);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id, durationInMinutes, title, contentUrl);
   }

   @Override
   public String toString() {
      return "Lesson{" +
              "id=" + id +
              ", durationInMinutes=" + durationInMinutes +
              ", title='" + title + '\'' +
              ", contentUrl='" + contentUrl + '\'' +
              '}';
   }
}

enum modeOfComm {
   EMAIL,
   SMS
}

class User {
   private static int auto_id = 0;
   private int id;
   private String name, email;
   private modeOfComm preferred;
   private String phoneNumber;

   User(String name, String email, modeOfComm preferred, String phoneNumber) {
      this.id = auto_id += 1;
      this.name = name;
      this.email = email;
      this.preferred = preferred;
      this.phoneNumber = phoneNumber;
   }

   modeOfComm getPreferredMode() {
      return this.preferred;
   }


   int getId() {
      return this.id;
   }

   String getName() {
      return this.name;
   }

   String getEmail() {
      return this.email;
   }

   String getPhoneNumber() {
      return this.phoneNumber;
   }

   @Override
   public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) return false;
      User user = (User) o;
      return id == user.id && Objects.equals(name, user.name) && Objects.equals(email, user.email);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id, name, email);
   }
}

class Instructor extends User {
   private ICourseRepo repo;

   Instructor(String name, String email, modeOfComm pref, ICourseRepo repo, String numb) {
      super(name, email, pref, numb);
      this.repo = repo;
   }

   List<Course> getCourses() {
      return this.repo.findByInstructor(this);
   }

}

class Student extends User {
   private IEnrollmentRepo repo;

   Student(String name, String email, modeOfComm pref, IEnrollmentRepo repo, String numb) {
      super(name, email, pref, numb);
      this.repo = repo;
   }

   List<Enrollment> getEnrollments() {
      return this.repo.findByStudent(this);
   }
}

interface ICourseRepo {
   void addCourse(int id, Course c, Instructor i);

   Course findById(int key);

   List<Course> findByInstructor(Instructor i);
}

class CourseRepository implements ICourseRepo {
   private final Map<Integer, Course> idCourseMap = new HashMap<>();
   private final Map<Instructor, List<Course>> instructorCourseMap = new HashMap<>();

   public void addCourse(int id, Course c, Instructor i) {
      if (idCourseMap.containsKey(id))
         System.out.println("There is already a course with same id. Please use a different id");
      else {
         idCourseMap.put(id, c);
         instructorCourseMap.putIfAbsent(i, new ArrayList<>());
         instructorCourseMap.get(i).add(c);
      }
   }

   public Course findById(int key) {
      if (!idCourseMap.containsKey(key)) {
         System.out.println("No course available for id= " + key);
         return null;
      }
      return idCourseMap.get(key);
   }

   public List<Course> findByInstructor(Instructor i) {
      return instructorCourseMap.getOrDefault(i, new ArrayList<>());
   }
}

enum EnrollmentStatus {
   Active,
   Completed
}

class Enrollment {
   private static int auto_id = 0;
   private int id;
   private Student s;
   private Course c;
   private EnrollmentStatus e;
   private Set<Lesson> lessonsWatched;
   private double progressPercent;

   Enrollment(Student s, Course c) {
      this.id = auto_id += 1;
      this.s = s;
      this.c = c;
      this.e = EnrollmentStatus.Active;
      this.lessonsWatched = new HashSet<>();
      this.progressPercent = 0.0D;
   }

   Student getStudent() {
      return this.s;
   }

   int id() {
      return this.id;
   }

   boolean completeLesson(Lesson l) {
      Set<Lesson> allLessons = c.getTotalLessons();
      if (!allLessons.contains(l))
         return false;
      if (lessonsWatched.contains(l))
         return false;
      lessonsWatched.add(l);
      this.progressPercent = (lessonsWatched.size() + 0.0D) / allLessons.size() * 100;
      if (Double.compare(this.progressPercent, 100.0) >= 0)
         this.e = EnrollmentStatus.Completed;
      return true;
   }

   boolean isCompleted() {
      return this.e == EnrollmentStatus.Completed;
   }


}

interface IEnrollmentRepo {
   void addEnrollment(Enrollment e);

   Enrollment findById(int id);

   List<Enrollment> findByStudent(Student s);
}

class EnrollmentRepo implements IEnrollmentRepo {
   private final Map<Integer, Enrollment> idEnrollmentMap = new HashMap<>();
   private final Map<Student, List<Enrollment>> studentEnrollmentMap = new HashMap<>();

   public void addEnrollment(Enrollment e) {
      Student s = e.getStudent();
      int key = e.id();
      if (idEnrollmentMap.containsKey(key))
         System.out.println("Enrollment with this Id already Exists");
      else {
         idEnrollmentMap.put(key, e);
         studentEnrollmentMap.putIfAbsent(s, new ArrayList<>());
         studentEnrollmentMap.get(s).add(e);
      }
   }

   @Override
   public Enrollment findById(int id) {
      Enrollment e = idEnrollmentMap.get(id);
      if (e == null) System.out.println("Enrollment with id does not exist");
      return e;
   }

   @Override
   public List<Enrollment> findByStudent(Student s) {
      return studentEnrollmentMap.getOrDefault(s, new ArrayList<>());
   }
}

interface Notify {
   void notify(String user, String message);
}

class NotificationFactory {
   public Notify type(modeOfComm source) {
      if (source == modeOfComm.EMAIL)
         return new EmailNotification();
      else if (source == modeOfComm.SMS)
         return new SMSNotification();
      throw new IllegalArgumentException("No valid service chosen for communication");
   }
}

class EmailNotification implements Notify {

   public void notify(String email, String message) {
      System.out.println(message);
   }
}

class SMSNotification implements Notify {

   public void notify(String phoneNumber, String message) {
      System.out.println(message);
   }
}

class EnrollmentService {
   private IEnrollmentRepo repo;
   private ICourseRepo cRepo;
   private NotificationFactory fact = new NotificationFactory();

   EnrollmentService(IEnrollmentRepo repo, ICourseRepo cRepo) {
      this.repo = repo;
      this.cRepo = cRepo;
   }

   void addCourse(Instructor i, Course c) {
      this.cRepo.addCourse(c.getID(), c, i);
   }

   int enroll(Student s, Course c) {
      Enrollment e = new Enrollment(s, c);
      modeOfComm pref = s.getPreferredMode();
      Notify n = fact.type(pref);
      n.notify(pref == modeOfComm.EMAIL ? s.getEmail() : s.getPhoneNumber(), "Your are successfully onboarded !");
      this.repo.addEnrollment(e);
      return e.id();
   }

   void completeLesson(Enrollment e, Lesson l) {
      if (!e.completeLesson(l)) {
         System.out.println("This lesson is not part of the course");
         return;
      }
      if (e.isCompleted()) {
         Student s = e.getStudent();
         modeOfComm pref = s.getPreferredMode();
         Notify n = fact.type(pref);
         n.notify(pref == modeOfComm.EMAIL ? s.getEmail() : s.getPhoneNumber(), "Congratulations ! You have successfully completed the course");
      }
   }
}

class Main {
   public static void main() {
      Lesson l0 = new Lesson(100, "DP", "DP"), l1 = new Lesson(100, "Seg tree", "Seg tree");
      Module M = new Module("DSA");
      M.addLesson(l0);
      M.addLesson(l1);
      Course c = new Course("CS 4th Sem", "Sem 4 curriculum for CS");
      c.addModule(M);
      ICourseRepo cRepo = new CourseRepository();
      Instructor i = new Instructor("Chamber", "Chamber", modeOfComm.EMAIL, cRepo,"+91");
      IEnrollmentRepo repo = new EnrollmentRepo();
      Student s = new Student("Viper", "Viper", modeOfComm.EMAIL, repo,"+91");
      EnrollmentService e = new EnrollmentService(repo, cRepo);
      e.addCourse(i, c);
      int id = e.enroll(s, c);
      e.completeLesson(repo.findById(id), l0);
      e.completeLesson(repo.findById(id), l1);
   }
}

