using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.GeneralUtilities
{
    public class Logger
    {
        private string _logDirPath;

        public string LogDirPath
        {
            get
            {
                return _logDirPath;
            }

            set
            {
                if (value == null) return;

                if (!Directory.Exists(value))
                    Directory.CreateDirectory(value);

                _logDirPath = value;
                _logFilePath = string.Format("{0}\\{1}.logs", value, DateTime.Now.ToString("yyyyMMdd"));
            }
        }

        private string _logFilePath;

        public async void Log(string log)
        {
            if (_logFilePath == null) return;

            using (StreamWriter outfile = new StreamWriter(_logFilePath, true))
            {
                await outfile.WriteAsync(string.Format("[{0}] {1}\n", DateTime.Now.ToString(), log));
            }
        }
    }
}
