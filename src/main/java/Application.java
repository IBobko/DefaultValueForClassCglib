import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Igor Bobko <limit-speed@yandex.ru>.
 */

class C {
    private String d;
    public void setD(String d) {
        this.d = d;
    }

    public String getD() {
        return d;
    }

    private String f;
    public void setF(String f) {
        this.f = f;
    }

    public String getF() {
        return f;
    }

    private Number n;
    public void setN(Number n) {
        this.n = n;
    }

    public Number getN() {
        return n;
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

    public void setB(B b) {
        this.b = b;
    }

    public B getB() {
        return b;
    }
}





public class Application {
    public static void main(String[] args) {
        A a = new A();

        //System.out.println(enhancer.create());

        m(a,"");

        System.out.println(a.getB().getC().getN());

        //assert(a.getB().getC().getD().equals("b_c_d"));

    }

    @SuppressWarnings("unchecked")
    public static void m(Object o,String path) {
        for (Method method: o.getClass().getMethods())
            if (method.getName().startsWith("get")) {
                String fieldName = method.getName().substring(3);
                if (fieldName.equals("Class")) continue;
                try {
                    Method setter = o.getClass().getMethod("set" + fieldName, method.getReturnType());
                    final String Path = path + fieldName;
                    Object o1;
                    if (method.getReturnType().isAssignableFrom(String.class)) {
                        o1 = "#" + Path;
                    } else if (method.getReturnType().isAssignableFrom(Number.class)) {
                        net.sf.cglib.proxy.Enhancer enhancer = new net.sf.cglib.proxy.Enhancer();
                        enhancer.setSuperclass(method.getReturnType());
                        enhancer.setCallback(new MethodInterceptor(){
                            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                                return "#" + Path;
                            }
                        });
                        o1 =  enhancer.create();
                    } else {
                        o1 = method.getReturnType().newInstance();
                    }
                    m(o1, Path + "_");
                    setter.invoke(o, o1);
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
