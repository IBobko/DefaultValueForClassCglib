import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Igor Bobko <limit-speed@yandex.ru>.
 */

class C {
    private String d;
    private String f;
    private Number n;

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getF() {
        return f;
    }

    public void setF(String f) {
        this.f = f;
    }

    public Number getN() {
        return n;
    }

    public void setN(Number n) {
        this.n = n;
    }
}

class B {
    private C c;

    public C getC() {
        return c;
    }

    public void setC(C c) {
        this.c = c;
    }
}

class A {
    private B b;

    public B getB() {
        return b;
    }

    public void setB(B b) {
        this.b = b;
    }
}


public class Application {
    public static void main(String[] args) {
        A a = new A();

        initializer(a, "");

        System.out.println(a.getB().getC().getN());

        //assert(a.getB().getC().getD().equals("b_c_d"));

    }

    @SuppressWarnings("unchecked")
    public static void initializer(Object o, String path) {
        for (Method method : o.getClass().getMethods())
            if (method.getName().startsWith("get")) {
                String fieldName = method.getName().substring(3);
                if (fieldName.equals("Class")) continue;
                try {
                    Method setter = o.getClass().getMethod("set" + fieldName, method.getReturnType());
                    final String Path = path + fieldName;
                    Object embedded;
                    if (method.getReturnType().isAssignableFrom(String.class)) {
                        embedded = "#" + Path;
                    } else if (method.getReturnType().isAssignableFrom(Number.class)) {
                        net.sf.cglib.proxy.Enhancer enhancer = new net.sf.cglib.proxy.Enhancer();
                        enhancer.setSuperclass(method.getReturnType());
                        enhancer.setCallback(new MethodInterceptor() {
                            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                                return "#" + Path;
                            }
                        });
                        embedded = enhancer.create();
                    } else {
                        embedded = method.getReturnType().newInstance();
                        initializer(embedded, Path + "_");
                    }
                    setter.invoke(o, embedded);

                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
    }
}
