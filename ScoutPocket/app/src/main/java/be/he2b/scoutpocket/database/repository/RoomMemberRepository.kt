package be.he2b.scoutpocket.database.repository

import android.content.Context
import be.he2b.scoutpocket.database.ScoutPocketDatabase
import be.he2b.scoutpocket.database.entity.Member
import be.he2b.scoutpocket.model.Section
import kotlinx.coroutines.flow.Flow

class RoomMemberRepository(context: Context): MemberRepository {

    private val db = ScoutPocketDatabase.getInstance(context)
    private val memberDao = db.memberDao()

    override suspend fun addMember(member: Member) {
        memberDao.insert(member)
    }

    override suspend fun updateMember(member: Member) {
        memberDao.update(member)
    }

    override suspend fun deleteMember(member: Member) {
        memberDao.delete(member)
    }

    override fun getAllMembers(): Flow<List<Member>> = memberDao.getAllMembers()

    override fun getMemberById(memberId: String): Flow<Member?> {
        return memberDao.getMemberById(memberId)
    }

    override fun getMembersBySection(section: Section): Flow<List<Member>> = memberDao.getMembersBySection(section)

}