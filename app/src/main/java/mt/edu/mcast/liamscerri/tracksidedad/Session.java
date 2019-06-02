package mt.edu.mcast.liamscerri.tracksidedad;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Session implements Serializable {
    private String _name;
    private Date _date;
    private List<Laptime> _list;
    private boolean _locked;
    private int _size;
    Session(){
        _list = new ArrayList<Laptime>();
        _locked = false;
        _date = new Date();
    }
    Session(long[] times){
        _list = new ArrayList<Laptime>();
        for (long time: times) {
            AddTime(time);
        }
        _locked = true;
        _date = new Date();
    }
    void AddTime(long time){
        if (!_locked) {
            _list.add(new Laptime(++_size, time));
        }
    }
    Laptime[] GetTimesArray() {
        return (Laptime[]) _list.toArray();
    }
    List<Laptime> GetTimesList() {
        return _list;
    }
    public void Lock(){
        for (Laptime lap: _list) {
            lap.Lock();
        }
        _locked = true;
    }
    public int GetSize(){
        return _size;
    }
    public Date GetDate(){
        return _date;
    }
    public String GetName(){
        return _name;
    }
    public void SetName(String name){
        _name = name;
    }
}
