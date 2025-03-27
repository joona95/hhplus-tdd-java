package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;
    private final UserLockManager userLockManager;

    public PointService(UserPointTable userPointTable, PointHistoryTable pointHistoryTable, UserLockManager userLockManager) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
        this.userLockManager = userLockManager;
    }

    public UserPoint getUserPointById(long id) {
        return userPointTable.selectById(id);
    }

    public List<PointHistory> getPointHistoriesByUserId(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    public UserPoint charge(long id, long amount) {

        ReentrantLock lock = userLockManager.getLock(id);
        lock.lock();
        try {
            UserPoint userPoint = userPointTable.selectById(id);
            userPoint = userPoint.charge(amount);

            userPoint = userPointTable.insertOrUpdate(userPoint.id(), userPoint.point());
            pointHistoryTable.insert(id, amount, TransactionType.CHARGE, userPoint.updateMillis());

            return userPoint;
        } finally {
            lock.unlock();
        }
    }

    public UserPoint use(long id, long amount) {

        ReentrantLock lock = userLockManager.getLock(id);
        lock.lock();
        try {
            UserPoint userPoint = userPointTable.selectById(id);
            userPoint = userPoint.use(amount);

            userPoint = userPointTable.insertOrUpdate(userPoint.id(), userPoint.point());
            pointHistoryTable.insert(id, amount, TransactionType.USE, userPoint.updateMillis());

            return userPoint;
        } finally {
            lock.unlock();
        }
    }
}
