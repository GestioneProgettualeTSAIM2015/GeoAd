using GeoAdServer.Domain.Contracts;
using GeoAdServer.Domain.Entities.Events;
using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace GeoAdServer.EventsHandling
{
    public class AndroidEventPropagator : IEventsHandler
    {
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
            Id = Utils.BuildRandomString(8);
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
            Log("performer starts running");
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
                    Log("performer stopped");
                }
            }
        }

        private void Handle(LocationCreated ievent)
        {
            NotifyAffected(ievent.Location.Lat, ievent.Location.Lng);
        }

        private void Handle(LocationUpdated ievent)
        {
            NotifyAffected(ievent.Location.Lat, ievent.Location.Lng);
        }

        private void Handle(LocationDeleted ievent)
        {
            NotifyAffected(ievent.Lat, ievent.Lng);
        }

        private void Handle(OfferingCreated ievent)
        {
            NotifyAffected(ievent.Lat, ievent.Lng);
        }

        private void Handle(OfferingUpdated ievent)
        {
            NotifyAffected(ievent.Lat, ievent.Lng);
        }

        private void Handle(OfferingDeleted ievent)
        {
            NotifyAffected(ievent.Lat, ievent.Lng);
        }

        private void NotifyAffected(string lat, string lng)
        {
            IEnumerable<string> keys = ((IEventsHandler) this).ChpHandler.GetKeysAffected(Double.Parse(lat), Double.Parse(lng));
            Push(keys);
        }

        private void Push(IEnumerable<string> keys)
        {
            Log("pushed to " + keys.Count() + " apps");
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
