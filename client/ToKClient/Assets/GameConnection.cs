using UnityEngine;
using System.Collections;
using System.Net; 
using System.Net.Sockets; 
using System.Text;

public class GameConnection : MonoBehaviour {
        UdpClient sendSocket;
        UdpClient recSocket;
        int listenport = 50050;
        
	// Use this for initialization
	void Start () {
            this.sendSocket = new UdpClient();
            this.recSocket = new UdpClient(listenport);
            this.sendSocket.Connect(PlayerPrefs.GetString("server"), 31338);
	}
	
	// Update is called once per frame
	void Update () {
            string buttonstring = "f|f|f|f|";
            if (Input.GetMouseButtonDown(0)){
                buttonstring = buttonstring + "t|";
            }
            else {
                buttonstring = buttonstring + "f|";
            }
            if (Input.GetMouseButtonDown(1)){
                buttonstring = buttonstring + "t|";
            }
            else {
                buttonstring = buttonstring + "f|";
            }
            Vector3 mousepos = Input.mousePosition;
            mousepos = Camera.main.ScreenToWorldPoint(mousepos);
            int x = (int) (mousepos.x * 100);
            int y = (int) (mousepos.y * 100);
            buttonstring = buttonstring + x + "|" + y;
            byte[] message = Encoding.UTF8.GetBytes(buttonstring);
            this.sendSocket.Send(message, message.Length);
        }
}
