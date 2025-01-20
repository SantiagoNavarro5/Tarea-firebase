package firebase1;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.concurrent.CountDownLatch;

public class FirebasePushUsuario {
    public static void main(String[] args) {
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("Ligia Jimenez");
        usuario.setApartamentoUsuario(7904);
        usuario.setemail("ligia@gmail.com");
        usuario.setContraseña("KSA1234");
       
        new FirebasePushUsuario().saveUsingPush(usuario);
    }
       
    private FirebaseDatabase firebaseDatabase;

    /**
     * inicialización de firebase.
     */
    private void initFirebase() {
       
        try {
           
            FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                    .setDatabaseUrl("https://ejemplo1-f971f-default-rtdb.firebaseio.com/")
                    .setServiceAccount(new FileInputStream(new File("C:\\Users\\locon\\OneDrive - Universidad Nacional de Colombia\\NetBeansProjects\\firebase\\prueba2-97bb1-firebase-adminsdk-mrv9r-e6d3c696cf.json")))

                    .build();

            FirebaseApp.initializeApp(firebaseOptions);
            firebaseDatabase = FirebaseDatabase.getInstance();
            System.out.println("La conexión se realizo exitosamente...");
           
        }catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        catch (RuntimeException ex) {  
            ex.printStackTrace();
        }
       
       
    }

   
    private void saveUsingPush(Usuario usuario) {
        if (usuario != null) {
            initFirebase();
           
            /* Get database root reference */
            DatabaseReference databaseReference = firebaseDatabase.getReference("/");
           
            /* Get existing child or will be created new child. */
            DatabaseReference childReference = databaseReference.child("CarpetaPrueba");

            /**
             * The Firebase Java client uses daemon threads, meaning it will not prevent a process from exiting.
             * So we'll wait(countDownLatch.await()) until firebase saves record. Then decrement `countDownLatch` value
             * using `countDownLatch.countDown()` and application will continues its execution.
             */
            CountDownLatch countDownLatch = new CountDownLatch(1);
           
            /**
             * push()
             * Add to a list of data in the database. Every time you push a new node onto a list,
             * your database generates a unique key, like items/unique-item-id/data
             */
            childReference.push().setValue(usuario, new DatabaseReference.CompletionListener() {

                @Override
                public void onComplete(DatabaseError de, DatabaseReference dr) {
                    System.out.println("Registro guardado!");
                    // decrement countDownLatch value and application will be continues its execution.
                    countDownLatch.countDown();
                }
            });
            try {
                //wait for firebase to saves record.
                countDownLatch.await();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}

