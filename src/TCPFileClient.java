import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TCPFileClient
{
    public static void main(String[] args) throws IOException
    {
        Scanner in = new Scanner(System.in);
        String ip = "localhost";
        final int port = 6666;
        Socket socket = null;
        try
        {
            socket = new Socket(ip, port);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream input = new DataInputStream(socket.getInputStream());
            out.writeUTF("link");
            boolean msg = input.readBoolean();
            if (msg)
            {
                System.out.println("link success");
                boolean f = true;
                while (f)
                {
                    System.out.println("1.upload\n2.download\n3.exit");
                    int choice = in.nextInt();
                    switch (choice)
                    {
                        case 1:
                        {
                            try
                            {
                                out.writeUTF("upload");
                                System.out.println("input file path:");
                                String uploadFileName = in.next();
                                out.writeUTF("/" + uploadFileName);
                                System.out.println("confirming");
                                boolean ret = input.readBoolean();
                                if (!ret)
                                {
                                    System.out.println("refuse");
                                    continue;
                                } else
                                {
                                    BufferedInputStream fin = new BufferedInputStream(
                                            new FileInputStream(new File(uploadFileName)));
                                    byte[] buffer = new byte[1024];
                                    System.out.println("uploading");
                                    while (fin.read(buffer) != -1)
                                    {
                                        out.write(buffer);
                                    }
                                    fin.close();
                                    System.out.println("finished");
                                }
                                byte[] endstr = "finished".getBytes("utf-8");
                                byte[] sendend = new byte[1024];
                                for (int i = 0; i < endstr.length; i++)
                                {
                                    sendend[i] = endstr[i];
                                }
                                out.write(sendend);
                            } catch (Exception e)
                            {
                                System.out.println(e.getMessage());
                            } finally
                            {
                                continue;
                            }
                        }
                        case 2:
                        {
                            out.writeUTF("download");
                            boolean candown = input.readBoolean();
                            if (candown)
                            {
                                System.out.println(input.readUTF());
                                int fileId = in.nextInt();
                                if (fileId == 0)
                                {
                                    out.writeInt(0);
                                    continue;
                                } else
                                {
                                    out.writeInt(fileId);
                                    String fname = input.readUTF();
                                    File df = new File("download/" + fname);
                                    if (!df.getParentFile().exists())
                                    {
                                        df.getParentFile().mkdir();
                                    }
                                    if (!df.exists())
                                    {
                                        df.createNewFile();
                                    }
                                    FileOutputStream fos = new FileOutputStream(df);
                                    byte[] buffer = new byte[1024];
                                    System.out.print("downloading");
                                    byte[] endstr = "finished".getBytes("utf-8");
                                    byte[] sendend = new byte[1024];
                                    for (int i = 0; i < endstr.length; i++)
                                    {
                                        sendend[i] = endstr[i];
                                    }
                                    while (input.read(buffer) != -1
                                            && !new String(buffer, "utf-8").equals(new String(sendend, "utf-8")))
                                    {
                                        fos.write(buffer);
                                        buffer = new byte[1024];
                                    }
                                    fos.close();
                                    System.out.println("finished");
                                    continue;
                                }
                            } else
                            {
                                System.out.println("empty");
                                continue;
                            }
                        }
                        default:
                        {
                            out.close();
                            input.close();
                            f = false;
                            System.out.println("exited");
                            break;
                        }
                    }
                }
            } else
            {
                System.out.println("link failure");
                try
                {
                    socket.close();
                } catch (IOException e)
                {
                    socket = null;
                    System.out.println(e.getMessage());
                }
            }
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
        } finally
        {
            socket.close();
        }
    }
}