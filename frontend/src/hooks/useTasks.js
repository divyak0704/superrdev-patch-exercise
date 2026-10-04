import { useState, useEffect } from 'react';
import { fetchTasks } from '../api';

export function useTasks(query, status, page, pageSize) {
  const [tasks, setTasks] = useState([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    // AbortController cancels the in-flight request when inputs change or the
    // component unmounts, so a slow old response can never overwrite a newer one.
    const controller = new AbortController();
    setLoading(true);
    setError(null);

    fetchTasks({ query, status, page, pageSize, signal: controller.signal })
      .then((data) => {
        setTasks(data.items);
        setTotal(data.total);
      })
      .catch((err) => {
        if (err.name === 'AbortError') return; // superseded, not a real failure
        setError(err.message);
        setTasks([]);
        setTotal(0);
      })
      .finally(() => {
        // Only the latest request is allowed to clear the loading flag.
        if (!controller.signal.aborted) setLoading(false);
      });

    return () => controller.abort();
  }, [query, status, page, pageSize]);

  return { tasks, total, loading, error };
}
