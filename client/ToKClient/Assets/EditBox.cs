using UnityEngine;
using UnityEngine.UI;
using System.Collections;

public class EditBox : MonoBehaviour {

	// Use this for initialization
	void Start () {
            GameObject canvas = GameObject.Find("InputField");
            InputField inputBox = canvas.GetComponent<InputField>();
            inputBox.onEndEdit.AddListener(storeString);
	}
	
	// Update is called once per frame
	void Update () {
	
	}
        
        void storeString(string s){
            PlayerPrefs.SetString("server", s);
        }
}
