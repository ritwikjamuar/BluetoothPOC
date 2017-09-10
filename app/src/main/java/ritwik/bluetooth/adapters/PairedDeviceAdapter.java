package ritwik.bluetooth.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ritwik.bluetooth.R;
import ritwik.bluetooth.models.DeviceInformation;

public class PairedDeviceAdapter extends RecyclerView.Adapter<PairedDeviceAdapter.DeviceViewHolder > {
    private Context mContext;
    private DeviceListener mListener;
    private List<DeviceInformation> mDeviceList;

    public PairedDeviceAdapter ( Context mContext, DeviceListener mListener, List<DeviceInformation> mDeviceList ) {
        this.mContext = mContext;
        this.mListener = mListener;
        this.mDeviceList = mDeviceList;
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView mDeviceCard;
        TextView mDeviceName, mDeviceMACAddress;

        public DeviceViewHolder ( View itemView ) {
            super ( itemView );
            initializeView ( itemView );
        }

        private void initializeView ( View view ) {
            mDeviceCard = (CardView) view.findViewById ( R.id.paired_device_card );
            mDeviceName = (TextView) view.findViewById ( R.id.paired_device_name );
            mDeviceMACAddress = (TextView) view.findViewById ( R.id.paired_device_mac_address );
            // Set on-Click Listener
            mDeviceCard.setOnClickListener ( DeviceViewHolder.this );
        }

        @Override public void onClick ( View view ) {
            mListener.onDeviceSelected ( mDeviceList.get ( getAdapterPosition () ) );
        }
    }

    @Override public DeviceViewHolder onCreateViewHolder ( ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from ( mContext ).inflate ( R.layout.list_devices, parent, false );
        return new DeviceViewHolder ( view );
    }

    @Override public void onBindViewHolder ( DeviceViewHolder holder, int position ) {
        holder.mDeviceName.setText ( mDeviceList.get ( position ).getDeviceName () );
        holder.mDeviceMACAddress.setText ( mDeviceList.get ( position ).getDeviceMACAddress () );
    }

    @Override public int getItemCount () {
        return mDeviceList.size ();
    }

    public void updateDeviceList ( List<DeviceInformation> deviceList ) {
        mDeviceList = deviceList;
        notifyDataSetChanged ();
        android.util.Log.e ( "Device List", "Updated" );
        android.util.Log.e ( "Device List", String.valueOf ( mDeviceList.size () ) );
    }

    public interface DeviceListener {
        void onDeviceSelected ( DeviceInformation information );
    }
}