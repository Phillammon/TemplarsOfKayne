using UnityEngine;
using System.Collections;

public class CameraPos : MonoBehaviour {
    // Use this for initialization
    void Start () {
        Vector3 ppos = GameObject.Find("Player").transform.position;
        Camera.main.transform.position = new Vector3(ppos.x, ppos.y, Camera.main.transform.position.z);
    }
    // Update is called once per frame
    void Update () {
        Vector3 mousepos = Input.mousePosition;
        float smoothing = 0.2f;
        Vector3 playerpos = GameObject.Find("Player").transform.position;
        mousepos = Camera.main.ScreenToWorldPoint(mousepos);
        Vector3 targetpos = Vector3.Lerp(playerpos, mousepos, 0.5f);
        Camera.main.transform.position = Vector3.Lerp(Camera.main.transform.position, new Vector3(targetpos.x, targetpos.y, Camera.main.transform.position.z), smoothing);
    }
}
