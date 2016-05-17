using UnityEngine;
using System.Collections;
using System.Net; 
using System.Net.Sockets; 
using System.Text;

public class ConnectButtonScript : MonoBehaviour {
        public static int listenport = 50050;
	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
	
	}
        public void onClick(){
            UdpClient sendSocket = new UdpClient();
            UdpClient recSocket = new UdpClient(listenport);
            recSocket.Client.ReceiveTimeout = 100;
            IPEndPoint endpoint = new IPEndPoint(IPAddress.Any, listenport);
            sendSocket.Connect(PlayerPrefs.GetString("server"), 31337);
            byte[] message = Encoding.UTF8.GetBytes("query");
            byte[] response;
            sendSocket.Send(message, message.Length);
            try{
                response = recSocket.Receive(ref endpoint);
                string responsestring = System.Text.Encoding.UTF8.GetString(response);
                Debug.Log(responsestring);
                if (responsestring == "Unknown Server Type"){
                    message = Encoding.UTF8.GetBytes("pick");
                    sendSocket.Send(message, message.Length);
                    Application.LoadLevel("PlayScene");
                }
            }
            catch (SocketException e){
                Debug.Log(e.ToString());
            }
            sendSocket.Close();
            recSocket.Close();
        }
}
