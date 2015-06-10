using GeoAdServer.Domain.Contracts;
using GeoAdServer.Domain.Entities.Events;
using Newtonsoft.Json;
using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Configuration;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace GeoAdServer.EventsHandling
{
    public class AndroidEventPropagator : IEventsHandler
    {
        private readonly static string _applicationId =
            ConfigurationManager.AppSettings["androidApplicationId"];

        IChangedPositionHandler IEventsHandler.ChpHandler { get; set; }

        public bool IsWorking { get; private set; }

        private static ConcurrentQueue<IEvent> _eventsQueue;

        public string Id { get; private set; }

        private Logger _logger;

        public AndroidEventPropagator(string logDirPath)
        {
            if (_eventsQueue == null) _eventsQueue = new ConcurrentQueue<IEvent>();

            _logger = new Logger()
            {
                LogDirPath = logDirPath
            };

            Id = Utils.BuildRandomString(6);

            Log("new AndroidEventPropagator");
        }

        private void Log(string message)
        {
            _logger.Log(Id + " => " + message);
        }

        void IEventsHandler.Enqueue(IEvent command)
        {
            _eventsQueue.Enqueue(command);
            if (!IsWorking)
            {
                IsWorking = true;
                new Thread(Process).Start();
            }
        }

        private void Process()
        {
            Log("aep starts running");
            while (IsWorking)
            {
                IEvent ievent;
                _eventsQueue.TryDequeue(out ievent);
                if (ievent != null)
                {
                    if (ievent is LocationCreated)
                    {
                        Handle(ievent as LocationCreated);
                    }
                    else if (ievent is LocationUpdated)
                    {
                        Handle(ievent as LocationUpdated);
                    }
                    else if (ievent is LocationDeleted)
                    {
                        Handle(ievent as LocationDeleted);
                    }
                    else if (ievent is OfferingCreated)
                    {
                        Handle(ievent as OfferingCreated);
                    }
                    else if (ievent is OfferingUpdated)
                    {
                        Handle(ievent as OfferingUpdated);
                    }
                    else if (ievent is OfferingDeleted)
                    {
                        Handle(ievent as OfferingDeleted);
                    }
                }
                else
                {
                    IsWorking = false;
                    Log("aep stopped");
                }
            }
        }

        private void Handle(LocationCreated ievent)
        {
            NotifyAffected("LocationCreated", JsonConvert.SerializeObject(ievent), ievent.Location.Lat, ievent.Location.Lng);
        }

        private void Handle(LocationUpdated ievent)
        {
            NotifyAffected("LocationUpdated", JsonConvert.SerializeObject(ievent), ievent.Location.Lat, ievent.Location.Lng);
        }

        private void Handle(LocationDeleted ievent)
        {
            NotifyAffected("LocationDeleted", JsonConvert.SerializeObject(ievent), ievent.Lat, ievent.Lng);
        }

        private void Handle(OfferingCreated ievent)
        {
            NotifyAffected("OfferingCreated", JsonConvert.SerializeObject(ievent), ievent.Lat, ievent.Lng);
        }

        private void Handle(OfferingUpdated ievent)
        {
            NotifyAffected("OfferingUpdated", JsonConvert.SerializeObject(ievent), ievent.Lat, ievent.Lng);
        }

        private void Handle(OfferingDeleted ievent)
        {
            NotifyAffected("OfferingDeleted", JsonConvert.SerializeObject(ievent), ievent.Lat, ievent.Lng);
        }

        private void NotifyAffected(string action, string message, string lat, string lng)
        {
            IEnumerable<string> keys = ((IEventsHandler) this).ChpHandler
                .GetKeysAffected(Double.Parse(lat), Double.Parse(lng));

            Push(action, message, new List<string>() { "aa", "ee" });
        }

        private readonly static int _MAX_KEYS_PER_PUSH = 1024;

        private void Push(string action, string message, IEnumerable<string> keys)
        {
            string pushTemplate = string.Format(
                "time_to_live=0&data.action={0}&data.message={1}&registration_ids=",
                action, message);

            for (int i = 0; i < keys.Count(); i += _MAX_KEYS_PER_PUSH)
            {
                var localKeys = keys.Skip(i).Take(_MAX_KEYS_PER_PUSH);

                string localKeysJson = JsonConvert.SerializeObject(localKeys);

                //send
                WebRequest request;
                request = WebRequest.Create("https://android.googleapis.com/gcm/send");
                request.Method = "post";
                request.ContentType = " application/x-www-form-urlencoded;charset=UTF-8";
                request.Headers.Add(string.Format("Authorization: key={0}", _applicationId));
                
                Byte[] byteArray = Encoding.UTF8.GetBytes(string.Format("{0}{1}", pushTemplate, localKeysJson));
                request.ContentLength = byteArray.Length;

                using (Stream pushStream = request.GetRequestStream())
                    pushStream.Write(byteArray, 0, byteArray.Length);

                Log(string.Format("pushed {0} to {1} apps", action, keys.Count()));

                //receive
                try
                {
                    using (HttpWebResponse response = request.GetResponse() as HttpWebResponse) { /* Success */ }
                }
                catch (WebException wEx)
                {
                    using (HttpWebResponse response = wEx.Response as HttpWebResponse)
                        using (StreamReader responseStreamReader = new StreamReader(response.GetResponseStream()))
                            Log(string.Format("push error {{{0}}}: \n>>>>>>>>>>\n{1}<<<<<<<<<<",
                                response.StatusCode.ToString(), responseStreamReader.ReadToEnd()));
                }
            }
        }
    }

    internal class Logger
    {
        private string _logDirPath;

        public string LogDirPath {
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

    internal class Utils
    {
        private static Random random = new Random((int)DateTime.Now.Ticks);

        public static string BuildRandomString(int size)
        {
            StringBuilder builder = new StringBuilder();
            char ch;
            for (int i = 0; i < size; i++)
            {
                ch = Convert.ToChar(Convert.ToInt32(Math.Floor(26 * random.NextDouble() + 97)));
                builder.Append(ch);
            }

            return builder.ToString();
        }
    }
}
