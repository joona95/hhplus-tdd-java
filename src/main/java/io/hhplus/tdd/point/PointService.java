package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public PointService(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    public UserPoint getUserPointById(long id) {
        return userPointTable.selectById(id);
    }

    public List<PointHistory> getPointHistoriesByUserId(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    public UserPoint charge(long id, long amount) {

        UserPoint userPoint = userPointTable.selectById(id);
        userPoint = userPoint.charge(amount);

        userPointTable.insertOrUpdate(userPoint.id(), userPoint.point());
        pointHistoryTable.insert(id, amount, TransactionType.CHARGE, userPoint.updateMillis());

        return userPoint;
    }

    public UserPoint use(long id, long amount) {

        UserPoint userPoint = userPointTable.selectById(id);
        userPoint = userPoint.use(amount);

        userPointTable.insertOrUpdate(userPoint.id(), userPoint.point());
        pointHistoryTable.insert(id, amount, TransactionType.USE, userPoint.updateMillis());

        return userPoint;
    }
}
