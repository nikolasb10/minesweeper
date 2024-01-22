import java.io.File;

public class InvalidDescriptionException extends Exception {
    public InvalidDescriptionException(String message, File file) {
        super(message);
        // delete the file
        boolean success = file.delete();
            
        if (success) {
            System.out.println("File deleted successfully.");
        } else {
            System.out.println("File deletion failed.");
        }
    }
}
