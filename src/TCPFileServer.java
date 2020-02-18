import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TCPFileServer
{
    public static void main(String[] args)
    {
        System.out.println("server started");
        TCPFileServer server = new TCPFileServer();
        server.init();
    }

    public void init()
    {
        try
        {
            final int port = 6666;
            ServerSocket serverSocket = new ServerSocket(port);
            while (true)
            {
                Socket client = serverSocket.accept();
                new HandlerThread(client);
            }
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    private static class HandlerThread implements Runnable
    {
        private Socket socket;

        public HandlerThread(Socket client)
        {
            socket = client;
            new Thread(this).start();
        }

        @Override
        public void run()
        {
            try
            {
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                String clientInputStr = input.readUTF();
                switch (clientInputStr)
                {
                    case "link":
                    {
                        out.writeBoolean(true);
                        System.out.println("client linked");
                        boolean rei = true;
                        while (rei)
                        {
                            clientInputStr = input.readUTF();
                            switch (clientInputStr)
                            {
                                case "upload":
                                {
                                    clientInputStr = input.readUTF();
                                    System.out.println("receive " + clientInputStr.substring(1) + "?(y/n)");
                                    char receive = new Scanner(System.in).nextLine().toCharArray()[0];
                                    receive = Character.toLowerCase(receive);
                                    if (receive == 'y')
                                    {
                                        out.writeBoolean(true);
                                        File f = new File("upload/" + clientInputStr
                                                .substring(clientInputStr.lastIndexOf("/") + 1));
                                        if (!f.getParentFile().exists())
                                        {
                                            f.getParentFile().mkdir();
                                        }
                                        if (!f.exists())
                                        {
                                            f.createNewFile();
                                        }
                                        FileOutputStream fos = new FileOutputStream(f);
                                        byte[] buffer = new byte[1024];
                                        System.out.println("receiving");
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
                                    } else
                                    {
                                        out.writeBoolean(false);
                                    }
                                    continue;
                                }
                                case "download":
                                {
                                    File uploadDir = new File("upload");
                                    File[] files = uploadDir.listFiles();
                                    int fileLength = files != null ? files.length : 0;
                                    if (uploadDir.exists() && fileLength > 0)
                                    {
                                        out.writeBoolean(true);
                                        String hint = fileLength + "files\n0.exit\n";
                                        for (int i = 0; i < files.length; i++)
                                        {
                                            hint += (i + 1) + "、" + files[i].getName() + "\n";
                                        }
                                        out.writeUTF(hint);
                                        int fileId = input.readInt();
                                        if (fileId == 0)
                                        {
                                            continue;
                                        }
                                        out.writeUTF(files[fileId - 1].getName());
                                        byte[] endstr = "finished".getBytes("utf-8");
                                        byte[] sendend = new byte[1024];
                                        for (int i = 0; i < endstr.length; i++)
                                        {
                                            sendend[i] = endstr[i];
                                        }
                                        FileInputStream fin = new FileInputStream(files[fileId - 1]);
                                        byte[] buffer = new byte[1024];
                                        System.out.println("sending");
                                        while (fin.read(buffer) != -1)
                                        {
                                            out.write(buffer);
                                        }
                                        fin.close();
                                        out.write(sendend);
                                        System.out.println("finished");
                                        continue;
                                    } else
                                    {
                                        out.writeBoolean(false);
                                    }
                                    continue;
                                }
                                default:
                                {
                                    rei = false;
                                    break;
                                }

                            }
                        }
                    }
                    default:
                        break;
                }
                out.close();
                input.close();
            } catch (Exception e)
            {
                System.out.println(e.getMessage());
            } finally
            {
                if (socket != null)
                {
                    try
                    {
                        socket.close();
                    } catch (Exception e)
                    {
                        socket = null;
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
    }
}