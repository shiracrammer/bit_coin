



package probit;
import com.intel.util.*;
import com.intel.langutil.ArrayUtils;
import java.io.*;
import com.intel.crypto.RsaAlg;



//
// Implementation of DAL Trusted Application: PROBIT 
//
// **************************************************************************************************
// NOTE:  This default Trusted Application implementation is intended for DAL API Level 7 and above
// **************************************************************************************************

public class BIT extends IntelApplet {

	/**
	 * This method will be called by the VM when a new session is opened to the Trusted Application 
	 * and this Trusted Application instance is being created to handle the new session.
	 * This method cannot provide response data and therefore calling
	 * setResponse or setResponseCode methods from it will throw a NullPointerException.
	 * 
	 * @param	request	the input data sent to the Trusted Application during session creation
	 * 
	 * @return	APPLET_SUCCESS if the operation was processed successfully, 
	 * 		any other error status code otherwise (note that all error codes will be
	 * 		treated similarly by the VM by sending "cancel" error code to the SW application).
	 */
	public int onInit(byte[] request) {
		DebugPrint.printString("Hello, DAL!");
		return APPLET_SUCCESS;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	int file=0;
	boolean onOrOff= false;
	/**
	 * This method will be called by the VM to handle a command sent to this
	 * Trusted Application instance.
	 * 
	 * @param	commandId	the command ID (Trusted Application specific) 
	 * @param	request		the input data for this command 
	 * @return	the return value should not be used by the applet
	 */
	
	public RsaAlg MyRsa=RsaAlg.create();//rsa to prezntation

	
	public int invokeCommand(int commandId, byte[] request) {
		
		DebugPrint.printString("Received command Id: " + commandId + ".");
		if(request != null)
		{
			DebugPrint.printString("Received buffer:");
			DebugPrint.printBuffer(request);
		}
		
		
		int res=0;
		 //reset system
		//FlashStorage.eraseFlashData(0);
		//FlashStorage.eraseFlashData(1);
		
		switch (commandId)  {
		case 1://מכניס סיסמא חדשה
		   res  = SetPassword(request);
		 
		   break;
		   
		case 2://log in
			   res =login(request);
			   break;
		case 3:
			   res = ResetPassword(request);
			   
			   break;
		case 4:
			  res = CurrencyTransfer(request);
			   break;
		
		case 5: res= logOut()	;   
			break;
		
		case 6:res=resetSystem();
		   
		
	}
	//	byte myRes[]= {};
		 setResponseCode(res);

		//final byte[] myResponse = { 'O', 'K' };

		
		
		/*
		 * To return the response data to the command, call the setResponse
		 * method before returning from this method. 
		 * Note that calling this method more than once will 
		 * reset the response data previously set.
		 */
		//setResponse(myResponse, 0, myResponse.length);

		/*
		 * In order to provide a return value for the command, which will be
		 * delivered to the SW application communicating with the Trusted Application,
		 * setResponseCode method should be called. 
		 * Note that calling this method more than once will reset the code previously set. 
		 * If not set, the default response code that will be returned to SW application is 0.
		 */
		//setResponseCode(commandId);

		/*
		 * The return value of the invokeCommand method is not guaranteed to be
		 * delivered to the SW application, and therefore should not be used for
		 * this purpose. Trusted Application is expected to return APPLET_SUCCESS code 
		 * from this method and use the setResposeCode method instead.
		 */
		return APPLET_SUCCESS;
	}

	private int logOut() {
		onOrOff=false;
		return 0;
	}
	private int ResetPassword(byte[] request) {
		if(onOrOff==false)
			return 0;
		FlashStorage.eraseFlashData(file);
	    FlashStorage.writeFlashData(file,request,0,request.length);
		
		return 1;
	}
	
	
	private int login(byte[] request) {
		byte [] arr = new byte[6];
		int size=FlashStorage.getFlashDataSize(0);
		DebugPrint.printInt(size);

		int destOff=0;	
		DebugPrint.printBuffer(request);
		FlashStorage.readFlashData(0,arr,destOff);
		DebugPrint.printBuffer(arr);
		for(int i=0;i<size;i++) {
			if(arr[i]!=request[i])
				 return 0;
		}
		
			onOrOff=true;
			DebugPrint.printString("!");
		    return 1;
		
		

		
	}
	
	public int SetPassword(byte [] request) {//ויצירת ארנק
		
		int size=FlashStorage.getFlashDataSize(0);
		if(size!=0) {
			return 0;
		}
		DebugPrint.printString("Length" + request.length);
		FlashStorage.writeFlashData(0,request,0,request.length);
		//creat keys:

		int size1=FlashStorage.getFlashDataSize(1);
		if(size1!=0) {
			return -1;
		}
	//	DebugPrint.printString("Length" + request.length);
		//ליצור מפתחות ולשמור לפלאש אחד
		
		short sizeKey=256;
		
		MyRsa.generateKeys(sizeKey);
		
		//saveKeys();
		short ee=0,mm=0,dd=0;
		
		/*
		mod - an array to hold the RSA key modulus (N)
		modIndex - index in the modulus array
		e - an array to hold the RSA key public exponent (E)
		eIndex - index in the public exponent array
		d - an array to hold the RSA key private exponent (D)
		dIndex - index in the private exponent array
		 */
		byte [] e =new byte[MyRsa.getPublicExponentSize()];//RSA key public
		
		byte [] mod=new byte[MyRsa.getModulusSize()];
		
		byte [] d=new byte[MyRsa.getPrivateExponentSize()];// RSA key private
		
		MyRsa.getKey(mod,mm,e,ee,d,dd);
		
		
		
		byte [] all =new byte[516];

		int j=0, k=0;
		  for (int i=0; i<516; i++)
          {
              if (i < 256)
                  all[i] = d[i];
              else if(i<512)
              {
            	  all[i] = mod[j];
            	  j++;
              }
              
              else {all[i] = e[k];
              k++;
              
              }

          }   
			
		 // FlashStorage.eraseFlashData(file1);
		  FlashStorage.writeFlashData(1,all,0,all.length);
		
		  
		// return Public Key to host						
		 setResponse(e, 0,e.length);
		
		return 1;
	}
	
	public static int convertByteArrayToInt2(byte[] bytes) {
		bytes = reverse(bytes, 4);
	    return ((bytes[0] & 0xFF) << 24) |
	            ((bytes[1] & 0xFF) << 16) |
	            ((bytes[2] & 0xFF) << 8) |
	            ((bytes[3] & 0xFF) << 0);
	}
	
	public static byte[] convertIntToByteArray2(int value) 
	{ 
	   return reverse(new byte[] {
	           (byte)(value >> 24),
	           (byte)(value >> 16),
	           (byte)(value >> 8),
	           (byte)value}, 4);
	}

	static byte[] reverse(byte a[], int n) 
	{ 
		byte[] b = new byte[n]; 
	    int j = n; 
	    for (int i = 0; i < n; i++) { 
	        b[j - 1] = a[i]; 
	        j = j - 1; 
	    } 
	    
	    return b;
	}
	
	
	public int resetSystem () {
		int size=FlashStorage.getFlashDataSize(0);
		int size1=FlashStorage.getFlashDataSize(1);
		if((size ==0)&&(size1==0))
		{
			return 0;
		}
		if(size!=0) {
			//return 0;
			FlashStorage.eraseFlashData(0);
		}
		if(size1!=0) {
			//return 0;
			FlashStorage.eraseFlashData(1);
		}
		onOrOff=false;//LOG OUT

			
			//FlashStorage.eraseFlashData(1);
			return 1;
		
	}
	
	public int CurrencyTransfer(byte[] request){
		if(onOrOff==false)
			return 0;
		
		/*
		byte[] recvPubKey = new byte[256] ;
		ArrayUtils.copyByteArray(request, 0, recvPubKey, 0, 256);
		*/
		
		byte[] bitcoin = new byte[256] ;
		ArrayUtils.copyByteArray(request, 256, bitcoin, 0, 256);
		
		//sign on bitcoin[] with pubKEY
		byte [] Mykey =new byte[516];
		FlashStorage.readFlashData(1,Mykey,0);
		//Mykey = request;
		byte [] e = new byte[4];
		byte [] mod=new byte[256];
		byte [] d=new byte[256];
		
		if(Mykey.length==0)
			return 0;
			
		int j=0,k=0;
		for(int i=0;i<Mykey.length ;i++)
		{
			if(i<256)
				d[i]=Mykey[i];
			else if(i<512)
			{
				mod[j]=Mykey[i];
				j++;
			}
			else { 
				e[k]=Mykey[i];
				k++;
			}
		}
			
		MyRsa.setKey(mod,(short) 0,(short)mod.length,e,(short) 0,
				(short)e.length,d,(short)0,(short)d.length);
		
		MyRsa.setHashAlg(RsaAlg.HASH_TYPE_SHA256);
		
		MyRsa.setPaddingScheme(RsaAlg.PAD_TYPE_PKCS1);
		
		short size=0,lengh=(short)bitcoin.length,size1=0;
		
			byte[] dataSign=new byte[256];
		
			MyRsa.signComplete(bitcoin,size,lengh,dataSign,size1);
			
			
			
			setResponse( dataSign, 0, dataSign.length);
			
		
		
			return 1;
		
		
		
		
	}
	
	
	
	/**
	 * This method will be called by the VM when the session being handled by
	 * this Trusted Application instance is being closed 
	 * and this Trusted Application instance is about to be removed.
	 * This method cannot provide response data and therefore
	 * calling setResponse or setResponseCode methods from it will throw a NullPointerException.
	 * 
	 * @return APPLET_SUCCESS code (the status code is not used by the VM).
	 */
	public int onClose() {
		DebugPrint.printString("Goodbye, DAL!");
		return APPLET_SUCCESS;
	}
}
