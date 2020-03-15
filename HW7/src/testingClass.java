import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;


@SuppressWarnings("unused")
public class testingClass {
    private static Method BeforeMethod = null;
    private static Method AfterMethod = null;
    private static HashMap<Method, Integer> methodsHashMap = new HashMap<>();

    public static void main(String[] args) {
        start(testedClass.class);
        clear();
        start(testedClass.class.getName());
    }

    private static void clear() {

    }

    public static void start(Class tClass) {

            fillMethods(tClass);
            try {
            doMethods(tClass);
            }
            catch (InstantiationException e){
                e.printStackTrace();
            }



    }

    private static void fillMethods(Class tClass) {
        System.out.println("Full Class name is " + tClass.getName());
        System.out.println("Simple Class name is " + tClass.getSimpleName());
        List<Method> beforeSuitList = new LinkedList<>();
        List<Method> afterSuitList = new LinkedList<>();
        Method[] methods = tClass.getMethods();

        for (Method o : methods) {
            o.setAccessible(true);
//            System.out.println(o.getReturnType() + " ||| " + o.getName() + " ||| " + Arrays.toString(o.getParameterTypes()));
            if (o.getAnnotation(BeforeSuite.class) != null) beforeSuitList.add(o);
            else if (o.getAnnotation(AfterSuite.class) != null) afterSuitList.add(o);
            else if (o.getAnnotation(Test.class) != null) methodsHashMap.put(o, o.getAnnotation(Test.class).priority());
        }
        if (beforeSuitList.size() > 1 || afterSuitList.size() > 1)
            throw new RuntimeException();
        else {
            BeforeMethod=beforeSuitList.get(0);
            AfterMethod=afterSuitList.get(0);
        }

    }

    public static void doMethods(Class tClass) throws InstantiationException{
        int i = 10, start = 0, end = 100;
        boolean bool = true;
        double doub = (double) i;
        float fl = (float) i;
        char c = 'i';
        String str = "iii";
        try {
            testedClass testInstance=(testedClass)tClass.newInstance();
            BeforeMethod.invoke(testInstance);
            methodsHashMap.entrySet().stream().sorted(Map.Entry.<Method, Integer>comparingByValue().reversed()).forEach(new Consumer<Map.Entry<Method, Integer>>() {
                @Override
                public void accept(Map.Entry<Method, Integer> methodIntegerEntry) {

                    Class[] type = methodIntegerEntry.getKey().getParameterTypes();
                    System.out.printf("Priority: %d. Action: ", methodIntegerEntry.getKey().getAnnotation(Test.class).priority());
                    try {
                        if (type[0] == int.class) methodIntegerEntry.getKey().invoke(testInstance,i);
                        else if (type[0] == boolean.class) methodIntegerEntry.getKey().invoke(testInstance,bool);
                        else if (type[0] == double.class) methodIntegerEntry.getKey().invoke(testInstance,doub);
                        else if (type[0] == float.class) methodIntegerEntry.getKey().invoke(testInstance,fl);
                        else if (type[0] == char.class) methodIntegerEntry.getKey().invoke(testInstance,c);
                        else if (type[0] == String.class) methodIntegerEntry.getKey().invoke(testInstance,str);

                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }

                }
            });
            AfterMethod.invoke(testInstance);

        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    public static void start(String className) {
        try {
            start(Class.forName(className));
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage() + " class not found");
        }
    }
}