using Intel.Dal;
using System;
using System.Text;

namespace PROBITHost
{
    public enum fanc
    {
        exit,
        SetPassword = 1,
        Log_In,
        ResetPassword,
        CurrencyTransfer,
        Log_Out,
        RESETsystem
    }

    class Program
    {
        static void Main(string[] args)
        {
#if AMULET
            // When compiled for Amulet the Jhi.DisableDllValidation flag is set to true 
			// in order to load the JHI.dll without DLL verification.
            // This is done because the JHI.dll is not in the regular JHI installation folder, 
			// and therefore will not be found by the JhiSharp.dll.
            // After disabling the .dll validation, the JHI.dll will be loaded using the Windows search path
			// and not by the JhiSharp.dll (see http://msdn.microsoft.com/en-us/library/7d83bc18(v=vs.100).aspx for 
			// details on the search path that is used by Windows to locate a DLL) 
            // In this case the JHI.dll will be loaded from the $(OutDir) folder (bin\Amulet by default),
			// which is the directory where the executable module for the current process is located.
            // The JHI.dll was placed in the bin\Amulet folder during project build.
            Jhi.DisableDllValidation = true;
#endif

            Jhi jhi = Jhi.Instance;
            JhiSession session;

            // This is the UUID of this Trusted Application (TA).
            //The UUID is the same value as the applet.id field in the Intel(R) DAL Trusted Application manifest.
            string appletID = "477eee28-794f-4c02-9114-fccfc75ac7b1";
            // This is the path to the Intel Intel(R) DAL Trusted Application .dalp file that was created by the Intel(R) DAL Eclipse plug-in.
            string appletPath = "C:/\\PROBIT\\bin\\PROBIT.dalp";

            // Install the Trusted Application
            Console.WriteLine("Installing the applet.");
            jhi.Install(appletID, appletPath);

            // Start a session with the Trusted Application
            byte[] initBuffer = new byte[] { }; // Data to send to the applet onInit function
            Console.WriteLine("Opening a session.");
            jhi.CreateSession(appletID, JHI_SESSION_FLAGS.None, initBuffer, out session);

            // Send and Receive data to/from the Trusted Application
            byte[] sendBuff = UTF32Encoding.UTF8.GetBytes("Hello"); // A message to send to the TA
            byte[] recvBuff= new byte[2000]; // A buffer to hold the output data from the TA
            int responseCode; // The return value that the TA provides using the IntelApplet.setResponseCode method
            //int cmdId = 1; // The ID of the command to be performed by the TA
            Console.WriteLine("Performing send and receive operation.");
            //jhi.SendAndRecv2(session, cmdId, sendBuff, ref recvBuff, out responseCode);
            //Console.Out.WriteLine("Response buffer is " + UTF32Encoding.UTF8.GetString(recvBuff));// to go ba

            recvBuff = new byte[2000];
            // sendBuff = new byte[10];
            byte[] publicKey = new byte[256];
            byte[] Bitcoin = UTF32Encoding.UTF8.GetBytes("11uEbMgunupShBVTewXjtqbBv5MndwfXhb");
            Console.Out.WriteLine("enter 1 to create a new wallet");
            Console.Out.WriteLine("enter 2 to log in");
            Console.Out.WriteLine("enter 3 to change password");
            Console.Out.WriteLine("enter 4 to Transfer Currency");
            Console.Out.WriteLine("enter 5 to log out");
            Console.Out.WriteLine("enter 6 to reset system");

            Console.Out.WriteLine("enter 0 to exit");
           // byte[] publicKEY = new byte[280];
           // string publicKEY;// = new byte[280];
            bool flag = false;
            int id = Convert.ToInt32(Console.ReadLine());

            while (id != 0)
            {
                if (flag)
                {
                    Console.Out.WriteLine("enter 1 to create a wallet");
                    Console.Out.WriteLine("enter 2 to log in");
                    Console.Out.WriteLine("enter 3 to change password");
                    Console.Out.WriteLine("enter 4 to Transfer Currency");
                    Console.Out.WriteLine("enter 5 to log out");
                    Console.Out.WriteLine("enter 6 to reset system");

                    Console.Out.WriteLine("enter 0 to exit");
                    id = Convert.ToInt32(Console.ReadLine());
                }
                else flag = true;

                switch (id)
                {
                    case (int)fanc.SetPassword://new password
                        recvBuff = new byte[2000];

                        Console.Out.WriteLine("please enter new password");
                        string myPass = Console.ReadLine();
                        sendBuff = new byte[UTF32Encoding.UTF8.GetBytes(myPass).Length];
                        sendBuff = UTF32Encoding.UTF8.GetBytes(myPass);
                        jhi.SendAndRecv2(session, id, sendBuff, ref recvBuff, out responseCode);
                        Console.Out.WriteLine("Response buffer is " + UTF32Encoding.UTF8.GetString(recvBuff));

                        publicKey = new byte[recvBuff.Length];
                        publicKey = recvBuff;
                        ////save public key
                        //Console.Out.WriteLine("Response buffer is " + UTF32Encoding.UTF8.GetString(publicKEY));
                        //publicKEY = recvBuff;
                        //Console.Out.WriteLine("Response buffer is " + UTF32Encoding.UTF8.GetString(publicKEY));
                        ////save to file or send to bob
                        ////alice is my walet


                        if (responseCode == 0)
                        {
                            Console.Out.WriteLine("Sorry but there is a password for the system");
                            Console.Out.WriteLine(" so you will not be able to add another password ");
                        }
                        else if (responseCode == 1)
                            Console.Out.WriteLine("Your password has been successfully added!!");
                        break;

                    case (int)fanc.Log_In:
                        Console.Out.WriteLine("please enter your password");
                        myPass = Console.ReadLine();
                        sendBuff = UTF32Encoding.UTF8.GetBytes(myPass);
                        jhi.SendAndRecv2(session, id, sendBuff, ref recvBuff, out responseCode);
                        if (responseCode == 0)
                        {
                            Console.Out.WriteLine("The password is incorrect");

                        }
                        else if (responseCode == 1)
                            Console.Out.WriteLine("You have successfully entered");

                        break;

                    case (int)fanc.ResetPassword:

                        Console.Out.WriteLine("Enter a new password:");
                        myPass = Console.ReadLine();
                        sendBuff = UTF32Encoding.UTF8.GetBytes(myPass);
                        jhi.SendAndRecv2(session, id, sendBuff, ref recvBuff, out responseCode);
                        if (responseCode == 0)
                        {
                            Console.Out.WriteLine("You are not allowed to change the password");

                        }
                        else if (responseCode == 1)
                            Console.Out.WriteLine("Password changed successfully:");


                        break;


                    case (int)fanc.CurrencyTransfer:

                        //Sending the public key of the wallet who gets the currency
                        //sendBuff = new byte[publicKey.Length];
                        sendBuff = new byte[516];
                        publicKey.CopyTo(sendBuff, 0);
                        Bitcoin.CopyTo(sendBuff, 256);

                        recvBuff = new byte[516];

                        jhi.SendAndRecv2(session, id, sendBuff, ref recvBuff, out responseCode);
                        string t2 = BitConverter.ToString(recvBuff, 0);
                        Console.Out.WriteLine(" new bitcoin block: " + t2);
                        //save block to file or send to coin network
                        if (responseCode == 0)
                        {
                            Console.Out.WriteLine("Currency Transfer failed;");
                        }
                        else if (responseCode == 1)
                        {
                            Console.Out.WriteLine("You have successfully Transfer Currency ");
                            byte[] newBitcoin = new byte[recvBuff.Length];
                            newBitcoin = recvBuff;
                            // save or sent new bitcoin to coin network
                        }
                        break;

                        /*  case (int)fanc.GetAcountInto:
                              jhi.SendAndRecv2(session, id, sendBuff, ref recvBuff, out responseCode);
                              string t2 = BitConverter.ToString(recvBuff, 0);
                              Console.Out.WriteLine(" username: " + responseCode);
                        */
                        
                    case (int)fanc.Log_Out:
                        Console.Out.WriteLine("Are you sure you want to log out?");
                        Console.Out.WriteLine("If so press 1 If not press 0");
                        int id1 = Convert.ToInt32(Console.ReadLine());
                        if (id1 == 1)
                        {
                            jhi.SendAndRecv2(session, id, sendBuff, ref recvBuff, out responseCode);
                            if (responseCode == 0)
                            {
                                Console.Out.WriteLine("You are not logged in");

                            }
                            else if (responseCode == 1)
                                Console.Out.WriteLine("You have successfully logged out");
                        }
                        break;


                    case (int)fanc.RESETsystem: 
                        Console.Out.WriteLine("Are you sure you want to reset system?");
                        Console.Out.WriteLine("If so press 1 If not press 0");
                        int id2 = Convert.ToInt32(Console.ReadLine());
                        if (id2 == 1)
                        {
                            jhi.SendAndRecv2(session, id, sendBuff, ref recvBuff, out responseCode);
                            if (responseCode == 0)
                            {
                                Console.Out.WriteLine("the system is not reset");

                            }
                            else if (responseCode == 1)
                                Console.Out.WriteLine("the system is reset");
                        }
                        break;

                    case (int)fanc.exit:
                        break;




                }
            }


            // Close the session
            Console.WriteLine("Closing the session.");
            jhi.CloseSession(session);

            //Uninstall the Trusted Application
            Console.WriteLine("Uninstalling the applet.");
            jhi.Uninstall(appletID);

            Console.WriteLine("Press Enter to finish.");
            Console.Read();
        }
    }
}