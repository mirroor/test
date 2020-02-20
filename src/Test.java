
public class Test
{
    static void t1(int[] a)
    {
        int length = a.length;
        int i;
        int r = 0;
        for (i = 0; i < length; i++)
        {
            r += a[i];
        }
        System.out.println(r);
    }

    static void t2(int[] a)
    {
        int length = a.length;
        int limit = length - 1;
        int i;
        int p = 10;
        int r = 0;
        for (i = 0; i < limit; i += p)
        {
            r = r + (a[i] + a[i+1] + a[i+2] + a[i+3] + a[i+4] + a[i+5] + a[i+6] + a[i+7] + a[i+8] + a[i+9]);
        }
        for (; i < length; i++)
        {
            r += a[i];
        }
        System.out.println(r);
    }

    public static void main(String[] args)
    {
        System.out.println("start");
        int length = 1000000000;
        int[] a = new int[length];
        for (int i = 0; i < length; i++)
        {
            a[i] = 1;
        }
        long start1 = System.currentTimeMillis();
        t1(a);
        long end1 = System.currentTimeMillis();
        System.out.println("t1:" + (end1 - start1));
        long start2 = System.currentTimeMillis();
        t2(a);
        long end2 = System.currentTimeMillis();
        System.out.println("t2:" + (end2 - start2));
    }
}
