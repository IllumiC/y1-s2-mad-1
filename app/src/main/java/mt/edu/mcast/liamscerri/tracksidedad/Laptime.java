package mt.edu.mcast.liamscerri.tracksidedad;
import java.io.Serializable;

public class Laptime implements Serializable {
    private long _time;
    private boolean _locked;
    private int _id;
    Laptime(int id){
        _time = 0;
        _locked = false;
        _id = id;
    }
    Laptime(int id, long time){
        _time = time;
        _locked = true;
        _id = id;
    }
    public long GetTime() {
        return _time;
    }
    public int GetId() {
        return _id;
    }
    public void SetTime(long time){
        if (!_locked){
            _time = time;
        }
    }
    public void Lock(){
        _locked = true;
    }
}
