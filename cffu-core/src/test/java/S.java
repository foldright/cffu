import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class S {
    public static void main(String[] args) {
        Map<Integer, Integer> result = Stream.of(1, 2, 3, 4, 5).collect(
                Collectors.groupingBy(
                        x -> x % 2,
                        Collectors.collectingAndThen(Collectors.toList(), List::size)
                ));
        System.out.println(result);
    }
}
