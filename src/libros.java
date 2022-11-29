import java.time.Duration;
import java.util.Random;

public class libros implements Runnable {

    // Los libros son el recurso compartido
    public static boolean[] libros = new boolean[9];
    public static Object o = new Object();

    public synchronized void reservaLibros(int libro1, int libro2) {
        while (libros[libro1] || libros[libro2]) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Reservo los libros
        libros[libro1] = true;
        libros[libro2] = true;
    }

    public synchronized void liberaLibros(int libro1, int libro2) {
        libros[libro1] = false;
        libros[libro2] = false;
        this.notifyAll();
    }

    @Override
    public void run() {
        try {
            while (true) {
                int libro1 = new Random().nextInt(9);
                int libro2 = new Random().nextInt(9);
                while (libro2 == libro1) {
                    libro2 = new Random().nextInt(9);
                }
                reservaLibros(libro1, libro2);

                System.out.println(
                        Thread.currentThread().getName() + " tiene reservados los libros " + libro1 + " y " + libro2);
                Thread.sleep(Duration.ofSeconds(new Random().nextInt(3, 5)));
                System.out.println(Thread.currentThread().getName() + " ha terminado de leer.");

                liberaLibros(libro1, libro2);

                Thread.sleep(Duration.ofSeconds(1));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        libros e = new libros();
        for (int i = 1; i <= 4; i++) {
            Thread hilo = new Thread(e);
            hilo.setName("Estudiante " + i);
            hilo.start();
            try {
                Thread.sleep(Duration.ofSeconds(1));
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}