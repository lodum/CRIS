package de.ifgi.lodum.util;

import java.io.*;

public class XmlCleaner extends FilterInputStream
{
    //this will take an input stream containing the raw xml data
    //we can then fix it up to avoid any problems with the
    //actual xml parsing by the rome libraries. 

    public XmlCleaner ( InputStream in )
    {
        super( in );
    }

    public int read ( byte [] b, int off, int len ) throws IOException
    {
        int bsRead = 0;
        boolean gotSome = false;
        int c = -1;

        while ( bsRead < len && ( c = read() ) > -1 ) 
        {
            gotSome = true;

            //replace any non-printing invalid xml characters
            //with a ?
            if ( c < 32 && c != 9 && c != 10 && c != 13 )
            {
                c = 63;
            }

            b[ off + ( bsRead++ ) ] = (byte) c;
        }

        return gotSome ? bsRead : -1;
    }
}