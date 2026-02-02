package be.he2b.scoutpocket.database.repository

import android.content.Context
import be.he2b.scoutpocket.database.ScoutPocketDatabase
import be.he2b.scoutpocket.database.entity.Member
import be.he2b.scoutpocket.model.Section
import kotlinx.coroutines.flow.Flow

class MemberRepository(context: Context) {

    private val db = ScoutPocketDatabase.getInstance(context)
    private val memberDao = db.memberDao()

    suspend fun addMember(member: Member) {
        memberDao.insert(member)
    }

    suspend fun updateMember(member: Member) {
        memberDao.update(member)
    }

    suspend fun deleteMember(member: Member) {
        memberDao.delete(member)
    }

    fun getAllMembers(): Flow<List<Member>> = memberDao.getAllMembers()

    fun getMemberById(memberId: String): Flow<Member?> {
        return memberDao.getMemberById(memberId)
    }

    fun getMembersBySection(section: Section): Flow<List<Member>> = memberDao.getMembersBySection(section)

}