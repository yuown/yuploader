package yuown.yuploader.ftp;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.ToNetASCIIOutputStream;
import org.apache.commons.net.io.Util;

public class YuploaderFTPClient extends FTPClient {

//	@Override
//	protected boolean _storeFile(String command, String remote, InputStream local) throws IOException {
//
//        Socket socket = _openDataConnection_(command, remote);
//
//        if (socket == null) {
//            return false;
//        }
//
//        OutputStream output = new BufferedOutputStream(socket.getOutputStream());
//
//
//        CSL csl = null;
//        if (__controlKeepAliveTimeout > 0) {
//            csl = new CSL(this, __controlKeepAliveTimeout, __controlKeepAliveReplyTimeout);
//        }
//
//        // Treat everything else as binary for now
//        try
//        {
//            Util.copyStream(local, output, getBufferSize(),
//                    CopyStreamEvent.UNKNOWN_STREAM_SIZE, __mergeListeners(csl),
//                    false);
//        }
//        catch (IOException e)
//        {
//            Util.closeQuietly(socket); // ignore close errors here
//            if (csl != null) {
//                csl.cleanUp(); // fetch any outstanding keepalive replies
//            }
//            throw e;
//        }
//
//        output.close(); // ensure the file is fully written
//        socket.close(); // done writing the file
//        if (csl != null) {
//            csl.cleanUp(); // fetch any outstanding keepalive replies
//        }
//        // Get the transfer response
//        boolean ok = completePendingCommand();
//        return ok;
//    
//	}

}
